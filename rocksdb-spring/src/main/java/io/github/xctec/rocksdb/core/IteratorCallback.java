package io.github.xctec.rocksdb.core;

/**
 * 迭代器回调
 *
 * @param <K>
 * @param <V>
 */
public interface IteratorCallback<K, V> {
    void callback(K key, V value, int count);
}
