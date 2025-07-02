package io.github.xctec.rocksdb.serializer;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * RocksDB 序列化接口
 *
 * @param <T> 数据类型
 */
public interface RocksdbSerializer<T> {

    /**
     * jdk序列化器
     *
     * @return jdk序列化器
     */
    static RocksdbSerializer<Object> java() {
        return java(null);
    }

    /**
     * jdk序列化器
     *
     * @param classLoader 指定ClassLoader
     * @return jdk序列化器
     */
    static RocksdbSerializer<Object> java(@Nullable ClassLoader classLoader) {
        return new JdkSerializationRocksdbSerializer(classLoader);
    }

    /**
     * 通用json序列化器
     *
     * @return 通用json序列化器
     */
    static RocksdbSerializer<Object> json() {
        return new GenericJackson2JsonRocksdbSerializer();
    }

    /**
     * 字符串序列化器
     *
     * @return 字符串序列化器
     */
    static RocksdbSerializer<String> string() {
        return StringRocksdbSerializer.UTF_8;
    }

    /**
     * 字节数组序列化器
     *
     * @return 字节数组序列化器
     */
    static RocksdbSerializer<byte[]> byteArray() {
        return new ByteArrayRocksdbSerializer();
    }

    /**
     * 序列化
     *
     * @param value 待序列化对象
     * @return 序列化数据
     */
    @Nullable
    byte[] serialize(@Nullable T value);


    /**
     * 反序列化
     *
     * @param bytes 序列化后数据
     * @return 反序列化对象
     */
    @Nullable
    T deserialize(@Nullable byte[] bytes);


    /**
     * 判断是否支持当前对象的序列化
     *
     * @param type 支持类型
     * @return 是否支持序列化
     */
    default boolean canSerialize(Class<?> type) {
        return ClassUtils.isAssignable(getTargetType(), type);
    }


    /**
     * 获取当前序列化支持的类型
     *
     * @return 支持的类型
     */
    default Class<?> getTargetType() {
        return Object.class;
    }
}
