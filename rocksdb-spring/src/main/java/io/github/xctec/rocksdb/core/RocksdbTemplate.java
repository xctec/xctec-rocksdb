package io.github.xctec.rocksdb.core;

import org.rocksdb.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * RocksdbTemplate的通用实现
 *
 * @param <K>
 * @param <V>
 */
public class RocksdbTemplate<K, V> extends AbstractRocksdbTemplate<K, V> implements ColumnFamilyOperations<K, V> {

    private Map<String, ColumnFamilyOperations> columnFamilyOperationsMap = new ConcurrentHashMap<>();

    public RocksdbTemplate() {
    }

    public RocksdbTemplate(String dbName, RocksDB db) {
        super(dbName, db);
    }

    public ColumnFamilyOperations<K, V> getDefaultColumnFamilyOperations() {
        return columnFamilyOperationsMap.get("default");
    }

    public Map<String, ColumnFamilyOperations> getColumnFamilyOperationsMap() {
        return columnFamilyOperationsMap;
    }

    public void setColumnFamilyOperationsMap(Map<String, ColumnFamilyOperations> columnFamilyOperationsMap) {
        this.columnFamilyOperationsMap.clear();
        this.columnFamilyOperationsMap.putAll(columnFamilyOperationsMap);
    }

    public <K1, V1> ColumnFamilyOperations<K1, V1> getColumnFamilyOperations(String columnFamilyName) {
        return columnFamilyOperationsMap.get(columnFamilyName);
    }

    public void setColumnFamilyOperations(String key, ColumnFamilyOperations columnFamilyOperations) {
        this.columnFamilyOperationsMap.put(key, columnFamilyOperations);
    }


    @Override
    public RocksIterator newIterator(ReadOptions readOptions) {
        return getDefaultColumnFamilyOperations().newIterator(readOptions);
    }

    @Override
    public V get(ReadOptions readOptions, K key) {
        return getDefaultColumnFamilyOperations().get(readOptions, key);
    }

    @Override
    public void put(WriteOptions writeOptions, K key, V value) {
        getDefaultColumnFamilyOperations().put(writeOptions, key, value);
    }

    @Override
    public void merge(WriteOptions writeOptions, byte[] key, byte[] value) {
        getDefaultColumnFamilyOperations().merge(writeOptions, key, value);
    }

    @Override
    public void merge(WriteOptions writeOptions, K key, V value) {
        getDefaultColumnFamilyOperations().merge(key, value);
    }

    @Override
    public void batch(WriteOptions writeOptions, Consumer<WriteBatch> writeBatchConsumer) {
        getDefaultColumnFamilyOperations().batch(writeOptions, writeBatchConsumer);
    }

    @Override
    public void batchPut(WriteOptions writeOptions, List<K> keys, List<V> values) {
        getDefaultColumnFamilyOperations().batchPut(writeOptions, keys, values);
    }

    @Override
    public void delete(WriteOptions writeOptions, K key) {
        getDefaultColumnFamilyOperations().delete(writeOptions, key);
    }

    @Override
    public void singleDelete(WriteOptions writeOptions, K key) {
        getDefaultColumnFamilyOperations().singleDelete(writeOptions, key);
    }

    @Override
    public void deleteRange(WriteOptions writeOptions, K startKey, K endKey) {
        getDefaultColumnFamilyOperations().deleteRange(writeOptions, startKey, endKey);
    }

    @Override
    public boolean exists(K key) {
        return getDefaultColumnFamilyOperations().exists(key);
    }

    @Override
    public void flush(boolean waitForFlush, boolean allowWriteStall) {
        getDefaultColumnFamilyOperations().flush(waitForFlush, allowWriteStall);
    }

    @Override
    public void iterator(ReadOptions readOptions, String startType, String seekKey, String order, IteratorCallback<K, V> iteratorCallback) {
        getDefaultColumnFamilyOperations().iterator(readOptions, startType, seekKey, order, iteratorCallback);
    }

    @Override
    public void close() {
        getDb().close();
    }
}
