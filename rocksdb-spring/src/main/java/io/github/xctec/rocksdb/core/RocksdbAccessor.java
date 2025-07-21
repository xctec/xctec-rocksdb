package io.github.xctec.rocksdb.core;

import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.Statistics;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocksdbTemplate的父类，维护db等
 */
public abstract class RocksdbAccessor implements InitializingBean {

    private String dbName;

    private RocksDB db;

    private boolean enableTransaction;

    private Statistics statistics;

    private Map<String, ColumnFamilyHandle> columnFamilies = new ConcurrentHashMap<>();

    public RocksdbAccessor() {
    }

    public RocksdbAccessor(String dbName, RocksDB db) {
        this.dbName = dbName;
        this.db = db;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        getRequiredDb();
    }

    public RocksDB getRequiredDb() {
        RocksDB db = getDb();
        Assert.state(db != null, "RocksDB instance is required");
        return db;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Nullable
    public RocksDB getDb() {
        return db;
    }

    public void setDb(@Nullable RocksDB db) {
        this.db = db;
    }

    public boolean isEnableTransaction() {
        return enableTransaction;
    }

    public void setEnableTransaction(boolean enableTransaction) {
        this.enableTransaction = enableTransaction;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Map<String, ColumnFamilyHandle> getColumnFamilies() {
        return columnFamilies;
    }

    public void setColumnFamilies(Map<String, ColumnFamilyHandle> columnFamilies) {
        this.columnFamilies.putAll(columnFamilies);
    }

    public void setColumnFamilies(Collection<ColumnFamilyHandle> columnFamilies) {
        for (ColumnFamilyHandle columnFamily : columnFamilies) {
            byte[] name = null;
            try {
                name = columnFamily.getName();
            } catch (Exception e) {

            }
            String key = new String(name);
            this.columnFamilies.put(key, columnFamily);
        }
    }
}
