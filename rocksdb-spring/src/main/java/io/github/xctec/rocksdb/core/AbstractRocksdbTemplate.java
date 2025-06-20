package io.github.xctec.rocksdb.core;

import org.rocksdb.RocksDB;

public class AbstractRocksdbTemplate<K, V> extends RocksdbAccessor {
    public AbstractRocksdbTemplate() {
    }

    public AbstractRocksdbTemplate(String dbName, RocksDB db) {
        super(dbName, db);
    }
}
