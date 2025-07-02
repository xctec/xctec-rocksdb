package io.github.xctec.rocksdb.core;

import org.rocksdb.RocksDB;

/**
 * Rocksdb的抽象类
 * @param <K>
 * @param <V>
 */
public class AbstractRocksdbTemplate<K, V> extends RocksdbAccessor {
    public AbstractRocksdbTemplate() {
    }

    public AbstractRocksdbTemplate(String dbName, RocksDB db) {
        super(dbName, db);
    }
}
