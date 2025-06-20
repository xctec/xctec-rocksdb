package io.github.xctec.rocksdb.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.xctec.rocksdb.exception.SerializationException;
import org.springframework.cache.support.NullValue;
import org.springframework.data.util.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

public class GenericJackson2JsonRocksdbSerializer implements RocksdbSerializer<Object> {

    private final ObjectMapper mapper;

    private final JacksonObjectReader reader;

    private final JacksonObjectWriter writer;

    private final Lazy<Boolean> defaultTypingEnabled;

    private final TypeResolver typeResolver;

    public GenericJackson2JsonRocksdbSerializer() {
        this((String) null);
    }

    public GenericJackson2JsonRocksdbSerializer(@Nullable String classPropertyTypeName) {
        this(classPropertyTypeName, JacksonObjectReader.create(), JacksonObjectWriter.create());
    }


    public GenericJackson2JsonRocksdbSerializer(@Nullable String classPropertyTypeName, JacksonObjectReader reader,
                                                JacksonObjectWriter writer) {

        this(new ObjectMapper(), reader, writer, classPropertyTypeName);

        // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
        // the type hint embedded for deserialization using the default typing feature.
        registerNullValueSerializer(mapper, classPropertyTypeName);

        StdTypeResolverBuilder typer = new TypeResolverBuilder(ObjectMapper.DefaultTyping.EVERYTHING,
                mapper.getPolymorphicTypeValidator());
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);

        if (StringUtils.hasText(classPropertyTypeName)) {
            typer = typer.typeProperty(classPropertyTypeName);
        }
        mapper.setDefaultTyping(typer);
    }

    public GenericJackson2JsonRocksdbSerializer(ObjectMapper mapper) {
        this(mapper, JacksonObjectReader.create(), JacksonObjectWriter.create());
    }

    public GenericJackson2JsonRocksdbSerializer(ObjectMapper mapper, JacksonObjectReader reader,
                                                JacksonObjectWriter writer) {
        this(mapper, reader, writer, null);
    }

    private GenericJackson2JsonRocksdbSerializer(ObjectMapper mapper, JacksonObjectReader reader,
                                                 JacksonObjectWriter writer, @Nullable String typeHintPropertyName) {

        Assert.notNull(mapper, "ObjectMapper must not be null");
        Assert.notNull(reader, "Reader must not be null");
        Assert.notNull(writer, "Writer must not be null");

        this.mapper = mapper;
        this.reader = reader;
        this.writer = writer;

        this.defaultTypingEnabled = Lazy.of(() -> mapper.getSerializationConfig().getDefaultTyper(null) != null);

        Supplier<String> typeHintPropertyNameSupplier;

        if (typeHintPropertyName == null) {

            typeHintPropertyNameSupplier = Lazy.of(() -> {
                if (defaultTypingEnabled.get()) {
                    return null;
                }

                return mapper.getDeserializationConfig().getDefaultTyper(null)
                        .buildTypeDeserializer(mapper.getDeserializationConfig(),
                                mapper.getTypeFactory().constructType(Object.class), Collections.emptyList())
                        .getPropertyName();

            }).or("@class");
        } else {
            typeHintPropertyNameSupplier = () -> typeHintPropertyName;
        }

        this.typeResolver = new TypeResolver(Lazy.of(mapper::getTypeFactory), typeHintPropertyNameSupplier);
    }

    public static void registerNullValueSerializer(ObjectMapper objectMapper, @Nullable String classPropertyTypeName) {

        // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
        // the type hint embedded for deserialization using the default typing feature.
        objectMapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(classPropertyTypeName)));
    }

    protected ObjectMapper getObjectMapper() {
        return this.mapper;
    }

    @Override
    public byte[] serialize(Object value) {
        if (value == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }

        try {
            return writer.write(mapper, value);
        } catch (IOException ex) {
            String message = String.format("Could not write JSON: %s", ex.getMessage());
            throw new SerializationException(message, ex);
        }
    }

    @Override
    public Object deserialize(byte[] source) {
        return deserialize(source, Object.class);
    }

    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {

        Assert.notNull(type,
                "Deserialization type must not be null Please provide Object.class to make use of Jackson2 default typing.");

        if (SerializationUtils.isEmpty(source)) {
            return null;
        }

        try {
            return (T) reader.read(mapper, source, resolveType(source, type));
        } catch (Exception ex) {
            String message = String.format("Could not read JSON:%s ", ex.getMessage());
            throw new SerializationException(message, ex);
        }
    }

    protected JavaType resolveType(byte[] source, Class<?> type) throws IOException {

        if (!type.equals(Object.class) || !defaultTypingEnabled.get()) {
            return typeResolver.constructType(type);
        }

        return typeResolver.resolveType(source, type);
    }

    static class TypeResolver {

        // need a separate instance to bypass class hint checks
        private final ObjectMapper mapper = new ObjectMapper();

        private final Supplier<TypeFactory> typeFactory;
        private final Supplier<String> hintName;

        TypeResolver(Supplier<TypeFactory> typeFactory, Supplier<String> hintName) {

            this.typeFactory = typeFactory;
            this.hintName = hintName;
        }

        protected JavaType constructType(Class<?> type) {
            return typeFactory.get().constructType(type);
        }

        protected JavaType resolveType(byte[] source, Class<?> type) throws IOException {

            JsonNode root = mapper.readTree(source);
            JsonNode jsonNode = root.get(hintName.get());

            if (jsonNode instanceof TextNode && jsonNode.asText() != null) {
                return typeFactory.get().constructFromCanonical(jsonNode.asText());
            }

            return constructType(type);
        }
    }

    private static class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        /**
         * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
         */
        NullValueSerializer(@Nullable String classIdentifier) {

            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }

        @Override
        public void serializeWithType(NullValue value, JsonGenerator gen, SerializerProvider serializers,
                                      TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }
    }

    private static class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        public TypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
            super(t, ptv);
        }

        @Override
        public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
            return this;
        }

        /**
         * Method called to check if the default type handler should be used for given type. Note: "natural types" (String,
         * Boolean, Integer, Double) will never use typing; that is both due to them being concrete and final, and since
         * actual serializers and deserializers will also ignore any attempts to enforce typing.
         */
        public boolean useForType(JavaType t) {

            if (t.isJavaLangObject()) {
                return true;
            }

            t = resolveArrayOrWrapper(t);

            if (t.isEnumType() || ClassUtils.isPrimitiveOrWrapper(t.getRawClass())) {
                return false;
            }

            /**
            if (t.isFinal() && !KotlinDetector.isKotlinType(t.getRawClass())
                    && t.getRawClass().getPackageName().startsWith("java")) {
                return false;
            }
             */

            // [databind#88] Should not apply to JSON tree models:
            return !TreeNode.class.isAssignableFrom(t.getRawClass());
        }

        private JavaType resolveArrayOrWrapper(JavaType type) {

            while (type.isArrayType()) {
                type = type.getContentType();
                if (type.isReferenceType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }

            while (type.isReferenceType()) {
                type = type.getReferencedType();
                if (type.isArrayType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }

            return type;
        }
    }
}
