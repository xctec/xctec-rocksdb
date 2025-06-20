package io.github.xctec.rocksdb.core;

import org.rocksdb.RocksDB;

public class StringRocksdbTemplate extends RocksdbTemplate<String, String>{
    public StringRocksdbTemplate() {

    }

    public StringRocksdbTemplate(String dbName, RocksDB db) {
        this();
        setDbName(dbName);
        setDb(db);
    }
}
