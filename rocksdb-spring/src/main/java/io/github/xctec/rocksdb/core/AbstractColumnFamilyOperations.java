package io.github.xctec.rocksdb.core;

import io.github.xctec.rocksdb.serializer.RocksdbSerializer;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;

public abstract class AbstractColumnFamilyOperations<K, V> implements ColumnFamilyOperations<K, V> {

    private String dbName;

    private RocksDB db;

    private String cfName;

    private ColumnFamilyHandle columnFamilyHandle;

    private @Nullable RocksdbSerializer keySerializer = null;

    private @Nullable RocksdbSerializer valueSerializer = null;

    private RocksdbSerializer stringSerializer = RocksdbSerializer.string();

    public AbstractColumnFamilyOperations() {

    }

    public AbstractColumnFamilyOperations(String dbName, RocksDB db, String cfName, ColumnFamilyHandle columnFamilyHandle) {
        this.dbName = dbName;
        this.db = db;
        this.cfName = cfName;
        this.columnFamilyHandle = columnFamilyHandle;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public RocksDB getDb() {
        return db;
    }

    public void setDb(RocksDB db) {
        this.db = db;
    }

    public String getCfName() {
        return cfName;
    }

    public void setCfName(String cfName) {
        this.cfName = cfName;
    }

    public ColumnFamilyHandle getColumnFamilyHandle() {
        return columnFamilyHandle;
    }

    public void setColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle) {
        this.columnFamilyHandle = columnFamilyHandle;
    }

    @Nullable
    public RocksdbSerializer getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(@Nullable RocksdbSerializer keySerializer) {
        this.keySerializer = keySerializer;
    }

    @Nullable
    public RocksdbSerializer getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(@Nullable RocksdbSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public RocksdbSerializer getStringSerializer() {
        return stringSerializer;
    }

    public void setStringSerializer(RocksdbSerializer stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    @SuppressWarnings("unchecked")
    byte[] rawKey(Object key) {

        Assert.notNull(key, "non null key required");

        if (getKeySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }

        return getKeySerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    byte[] rawString(String key) {
        return getStringSerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    byte[] rawValue(Object value) {

        if (getValueSerializer() == null && value instanceof byte[]) {
            return (byte[]) value;
        }

        return getValueSerializer().serialize(value);
    }

    byte[][] rawValues(Object... values) {

        byte[][] rawValues = new byte[values.length][];
        int i = 0;
        for (Object value : values) {
            rawValues[i++] = rawValue(value);
        }

        return rawValues;
    }

    byte[][] rawValues(Collection<V> values) {

        Assert.notEmpty(values, "Values must not be 'null' or empty");
        Assert.noNullElements(values.toArray(), "Values must not contain 'null' value");

        byte[][] rawValues = new byte[values.size()][];
        int i = 0;
        for (V value : values) {
            rawValues[i++] = rawValue(value);
        }

        return rawValues;
    }

    @SuppressWarnings("unchecked")
    K deserializeKey(byte[] value) {
        if (getKeySerializer() == null) {
            return (K) value;
        }
        return (K) getKeySerializer().deserialize(value);
    }


    @SuppressWarnings("unchecked")
    V deserializeValue(byte[] value) {
        if (getValueSerializer() == null) {
            return (V) value;
        }
        return (V) getValueSerializer().deserialize(value);
    }

    String deserializeString(byte[] value) {
        return (String) getStringSerializer().deserialize(value);
    }
}
