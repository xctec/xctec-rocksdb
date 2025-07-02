package io.github.xctec.rocksdb.serializer;

import io.github.xctec.rocksdb.exception.SerializationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Jdk Rocksdb序列化器
 */
public class JdkSerializationRocksdbSerializer implements RocksdbSerializer<Object> {

    private final Converter<Object, byte[]> serializer;
    private final Converter<byte[], Object> deserializer;


    public JdkSerializationRocksdbSerializer() {
        this(new SerializingConverter(), new DeserializingConverter());
    }


    public JdkSerializationRocksdbSerializer(@Nullable ClassLoader classLoader) {
        this(new SerializingConverter(), new DeserializingConverter(classLoader));
    }

    public JdkSerializationRocksdbSerializer(Converter<Object, byte[]> serializer, Converter<byte[], Object> deserializer) {

        Assert.notNull(serializer, "Serializer must not be null");
        Assert.notNull(deserializer, "Deserializer must not be null");

        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public byte[] serialize(@Nullable Object value) {

        if (value == null) {
            return SerializationUtils.EMPTY_ARRAY;
        }

        try {
            return serializer.convert(value);
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }

    @Override
    public Object deserialize(@Nullable byte[] bytes) {

        if (SerializationUtils.isEmpty(bytes)) {
            return null;
        }

        try {
            return deserializer.convert(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }
}
