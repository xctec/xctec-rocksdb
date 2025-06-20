package io.github.xctec.rocksdb.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.xctec.rocksdb.exception.SerializationException;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Jackson2JsonRocksdbSerializer<T> implements RocksdbSerializer<T> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final JavaType javaType;
    private final JacksonObjectReader reader;
    private final JacksonObjectWriter writer;
    private ObjectMapper mapper;

    public Jackson2JsonRocksdbSerializer(Class<T> type) {
        this(new ObjectMapper(), type);
    }

    public Jackson2JsonRocksdbSerializer(JavaType javaType) {
        this(new ObjectMapper(), javaType);
    }

    public Jackson2JsonRocksdbSerializer(ObjectMapper mapper, Class<T> type) {

        Assert.notNull(mapper, "ObjectMapper must not be null");
        Assert.notNull(type, "Java type must not be null");

        this.javaType = getJavaType(type);
        this.mapper = mapper;
        this.reader = JacksonObjectReader.create();
        this.writer = JacksonObjectWriter.create();
    }


    public Jackson2JsonRocksdbSerializer(ObjectMapper mapper, JavaType javaType) {
        this(mapper, javaType, JacksonObjectReader.create(), JacksonObjectWriter.create());
    }


    public Jackson2JsonRocksdbSerializer(ObjectMapper mapper, JavaType javaType, JacksonObjectReader reader,
                                         JacksonObjectWriter writer) {

        Assert.notNull(mapper, "ObjectMapper must not be null!");
        Assert.notNull(reader, "Reader must not be null!");
        Assert.notNull(writer, "Writer must not be null!");

        this.mapper = mapper;
        this.reader = reader;
        this.writer = writer;
        this.javaType = javaType;
    }

    public void setObjectMapper(ObjectMapper mapper) {
        Assert.notNull(mapper, "'objectMapper' must not be null");
        this.mapper = mapper;
    }

    @Override
    public byte[] serialize(T value) {
        if (value == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }
        try {
            return this.writer.write(this.mapper, value);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            return (T) this.reader.read(this.mapper, bytes, javaType);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }
}
