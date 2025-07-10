package io.github.xctec.rocksdb.core;

import org.rocksdb.ReadOptions;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

import java.util.List;
import java.util.function.Consumer;

/**
 * ColumnFamily操作
 *
 * @param <K> key类型
 * @param <V> value类型
 */
public interface ColumnFamilyOperations<K, V> {

    default V get(K key) {
        try (ReadOptions readOptions = new ReadOptions()) {
            return get(readOptions, key);
        }
    }

    RocksIterator newIterator(ReadOptions readOptions);

    V get(ReadOptions readOptions, K key);

    default void put(K key, V value) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            put(writeOptions, key, value);
        }
    }

    void put(WriteOptions writeOptions, K key, V value);

    default void merge(byte[] key, byte[] value) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            merge(writeOptions, key, value);
        }
    }

    void merge(WriteOptions writeOptions, byte[] key, byte[] value);

    default void merge(K key, V value) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            merge(writeOptions, key, value);
        }
    }

    void merge(WriteOptions writeOptions, K key, V value);

    default void batch(Consumer<WriteBatch> writeBatchConsumer) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            batch(writeOptions, writeBatchConsumer);
        }
    }

    void batch(WriteOptions writeOptions, Consumer<WriteBatch> writeBatchConsumer);

    default void batchPut(List<K> keys, List<V> values) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            batchPut(writeOptions, keys, values);
        }
    }

    void batchPut(WriteOptions writeOptions, List<K> keys, List<V> values);

    default void delete(K key) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            delete(writeOptions, key);
        }
    }

    void delete(WriteOptions writeOptions, K key);

    default void singleDelete(K key) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            singleDelete(writeOptions, key);
        }
    }

    void singleDelete(WriteOptions writeOptions, K key);

    default void deleteRange(K startKey, K endKey) {
        try (WriteOptions writeOptions = new WriteOptions()) {
            deleteRange(writeOptions, startKey, endKey);
        }
    }

    void deleteRange(WriteOptions writeOptions, K startKey, K endKey);

    boolean exists(K key);

    void flush(boolean waitForFlush, boolean allowWriteStall);

    default void flush() {
        flush(true, true);
    }

    void iterator(ReadOptions readOptions, String startType, String seekKey, String order, IteratorCallback<K, V> iteratorCallback);

    default void iterator(String startType, String seekKey, String order, IteratorCallback<K, V> iteratorCallback) {
        try (ReadOptions readOptions = new ReadOptions()) {
            iterator(readOptions, startType, seekKey, order, iteratorCallback);
        }
    }

    void close();
}
