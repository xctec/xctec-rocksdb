package io.github.xctec.rocksdb.serializer;

import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 序列化工具类
 */
public class SerializationUtils {

    static final byte[] EMPTY_ARRAY = new byte[0];

    /**
     * 判断数据是否为孔
     *
     * @param data
     * @return
     */
    static boolean isEmpty(@Nullable byte[] data) {
        return (data == null || data.length == 0);
    }

    /**
     * 反序列化为集合
     *
     * @param rawValues         字节数据列表
     * @param type              类型
     * @param rocksdbSerializer 序列化器
     * @param <T>               数据类型
     * @return 返回的集合
     */
    @SuppressWarnings("unchecked")
    static <T extends Collection<?>> T deserializeValues(@Nullable Collection<byte[]> rawValues, Class<T> type,
                                                         @Nullable RocksdbSerializer<?> rocksdbSerializer) {
        // connection in pipeline/multi mode
        if (rawValues == null) {
            return (T) CollectionFactory.createCollection(type, 0);
        }

        Collection<Object> values = (List.class.isAssignableFrom(type) ? new ArrayList<>(rawValues.size())
                : new LinkedHashSet<>(rawValues.size()));
        for (byte[] bs : rawValues) {
            values.add(rocksdbSerializer.deserialize(bs));
        }

        return (T) values;
    }

    /**
     * 反序列化
     *
     * @param rawValues         数据
     * @param rocksdbSerializer 序列化器
     * @param <T>               数据类型
     * @return 结果set
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> deserialize(@Nullable Set<byte[]> rawValues, @Nullable RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, Set.class, rocksdbSerializer);
    }

    /**
     * 反序列化
     *
     * @param rawValues         数据
     * @param rocksdbSerializer 序列化器
     * @param <T>               数据类型
     * @return 数据列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(@Nullable List<byte[]> rawValues,
                                          @Nullable RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, List.class, rocksdbSerializer);
    }

    /**
     * 反序列化
     *
     * @param rawValues         数据
     * @param rocksdbSerializer 序列化器
     * @param <T>               结果类型
     * @return 结果集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserialize(@Nullable Collection<byte[]> rawValues,
                                                RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, List.class, rocksdbSerializer);
    }

    /**
     * 反序列化为map
     *
     * @param rawValues         数据
     * @param rocksdbSerializer 序列化器
     * @param <T>               数据类型
     * @return 结果map
     */
    public static <T> Map<T, T> deserialize(@Nullable Map<byte[], byte[]> rawValues, RocksdbSerializer<T> rocksdbSerializer) {

        if (rawValues == null) {
            return Collections.emptyMap();
        }
        Map<T, T> ret = new LinkedHashMap<>(rawValues.size());
        for (Map.Entry<byte[], byte[]> entry : rawValues.entrySet()) {
            ret.put(rocksdbSerializer.deserialize(entry.getKey()), rocksdbSerializer.deserialize(entry.getValue()));
        }
        return ret;
    }
}
