package io.github.xctec.rocksdb.core;

import org.rocksdb.RocksDB;

/**
 * 使用字符串序列化的RocksdbTemplate
 */
public class StringRocksdbTemplate extends RocksdbTemplate<String, String> {
    public StringRocksdbTemplate() {

    }

    public StringRocksdbTemplate(String dbName, RocksDB db) {
        this();
        setDbName(dbName);
        setDb(db);
    }

    @Override
    public ColumnFamilyOperations<String, String> getColumnFamilyOperations(String columnFamilyName) {
        return super.getColumnFamilyOperations(columnFamilyName);
    }
}
