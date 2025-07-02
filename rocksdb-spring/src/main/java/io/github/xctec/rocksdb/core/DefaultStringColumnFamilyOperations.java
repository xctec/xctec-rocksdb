package io.github.xctec.rocksdb.core;

import io.github.xctec.rocksdb.serializer.RocksdbSerializer;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;

/**
 * ColumnFamilyOperations的默认实现，并使用字符串序列化
 */
public class DefaultStringColumnFamilyOperations extends DefaultColumnFamilyOperations<String, String> {
    public DefaultStringColumnFamilyOperations() {
        setKeySerializer(RocksdbSerializer.string());
        setValueSerializer(RocksdbSerializer.string());
    }

    public DefaultStringColumnFamilyOperations(String dbName, RocksDB db, String cfName, ColumnFamilyHandle columnFamilyHandle) {
        super(dbName, db, cfName, columnFamilyHandle);
        setKeySerializer(RocksdbSerializer.string());
        setValueSerializer(RocksdbSerializer.string());
    }
}
