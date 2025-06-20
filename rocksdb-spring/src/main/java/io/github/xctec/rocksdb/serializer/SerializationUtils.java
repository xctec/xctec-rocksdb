package io.github.xctec.rocksdb.serializer;

import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;

import java.util.*;

public class SerializationUtils {

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(@Nullable byte[] data) {
        return (data == null || data.length == 0);
    }

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

    @SuppressWarnings("unchecked")
    public static <T> Set<T> deserialize(@Nullable Set<byte[]> rawValues, @Nullable RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, Set.class, rocksdbSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(@Nullable List<byte[]> rawValues,
                                          @Nullable RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, List.class, rocksdbSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserialize(@Nullable Collection<byte[]> rawValues,
                                                RocksdbSerializer<T> rocksdbSerializer) {
        return deserializeValues(rawValues, List.class, rocksdbSerializer);
    }

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
