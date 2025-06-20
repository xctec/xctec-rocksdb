package io.github.xctec.rocksdb.serializer;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public interface RocksdbSerializer<T> {

    static RocksdbSerializer<Object> java() {
        return java(null);
    }

    static RocksdbSerializer<Object> java(@Nullable ClassLoader classLoader) {
        return new JdkSerializationRocksdbSerializer(classLoader);
    }

    static RocksdbSerializer<Object> json() {
        return new GenericJackson2JsonRocksdbSerializer();
    }

    static RocksdbSerializer<String> string() {
        return StringRocksdbSerializer.UTF_8;
    }

    static RocksdbSerializer<byte[]> byteArray() {
        return new ByteArrayRocksdbSerializer();
    }

    @Nullable
    byte[] serialize(@Nullable T value);


    @Nullable
    T deserialize(@Nullable byte[] bytes);


    default boolean canSerialize(Class<?> type) {
        return ClassUtils.isAssignable(getTargetType(), type);
    }


    default Class<?> getTargetType() {
        return Object.class;
    }
}
