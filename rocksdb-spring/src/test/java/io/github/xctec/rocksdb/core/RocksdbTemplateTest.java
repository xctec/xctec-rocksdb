package io.github.xctec.rocksdb.core;

import io.github.xctec.rocksdb.builder.RocksdbTemplateBuilder;
import org.rocksdb.RocksDBException;
import org.testng.annotations.Test;

public class RocksdbTemplateTest {

    @Test
    public void test() throws RocksDBException {

        RocksdbTemplate rocksdbTemplate = RocksdbTemplateBuilder.builder(StringRocksdbTemplate.class, DefaultStringColumnFamilyOperations.class)
                .setDbName("test")
                .setPath("rocksdb")
                .setDbOptionsConfigurer(
                        x -> x.setCreateIfMissing(true)
                                .setCreateMissingColumnFamilies(true)
                )
                .setDefaultColumnFamilyOptionsConfigurer(x -> {
                }, x -> {
                })
                .addColumnFamily("cf1", null, null)
                .addColumnFamily("cf2", null, null)
                .addColumnFamily("cf3", null, null)
                .addColumnFamily("cf4", null, null)
                .build();
        rocksdbTemplate.put("abc", "abc");
        rocksdbTemplate.put("def", "def");
        Object abc = rocksdbTemplate.get("abc");
        System.out.println(abc);
    }
}