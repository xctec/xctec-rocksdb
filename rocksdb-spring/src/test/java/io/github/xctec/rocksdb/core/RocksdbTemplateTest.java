package io.github.xctec.rocksdb.core;

import io.github.xctec.rocksdb.builder.ColumnFamilyConfigurer;
import io.github.xctec.rocksdb.builder.DBOptionsConfigurer;
import io.github.xctec.rocksdb.builder.RocksdbTemplateBuilder;
import io.github.xctec.rocksdb.serializer.RocksdbSerializer;
import org.rocksdb.RocksDBException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RocksdbTemplateTest {

    @Test
    public void test() throws RocksDBException {

        DBOptionsConfigurer dbOptionsConfigurer = x -> x.setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
        ColumnFamilyConfigurer defaultColumnFamilyConfigurer = new ColumnFamilyConfigurer("default");

        // 使用字符串序列化key和value
        StringRocksdbTemplate rocksdbTemplate = RocksdbTemplateBuilder.builder(StringRocksdbTemplate.class, DefaultStringColumnFamilyOperations.class)
                .setDbName("test")   // 设置名称
                .setPath("rocksdb")    // rocksdb目录
                .setDbOptionsConfigurer(dbOptionsConfigurer)  // rocksdb配置
                .setDefaultColumnFamilyOptionsConfigurer(defaultColumnFamilyConfigurer) // 默认cf配置
                .addColumnFamily("cf1", null, null)  // 增加cf1,使用默认配置
                .addColumnFamily("cf2", null, null)  // 增加cf2,使用默认配置
                .addColumnFamily("cf3", null, null)  // 增加cf3,使用默认配置
                .addColumnFamily("cf4", null, null)  // 增加cf4,使用默认配置
                .build();
        // 利用rocksdbTemplate进行操作
        // 设置key 默认cf
        rocksdbTemplate.put("k1", "v1");
        rocksdbTemplate.put("k2", "v2");
        // 获取key
        Object v1 = rocksdbTemplate.get("k1");
        System.out.println(v1);

        // 指定cf操作
        rocksdbTemplate.getColumnFamilyOperations("cf1").put("k21", "v21");
        rocksdbTemplate.getColumnFamilyOperations("cf1").put("k22", "v22");
        rocksdbTemplate.getColumnFamilyOperations("cf1").put("k23", "v23");
        String v22 = rocksdbTemplate.getColumnFamilyOperations("cf1").get("k22");
        System.out.println(v22);

        // s
        defaultColumnFamilyConfigurer = new ColumnFamilyConfigurer("default", x1 -> {

        }, x2 -> {
            x2.setKeySerializer(RocksdbSerializer.string());
            x2.setValueSerializer(RocksdbSerializer.java());
        });


        RocksdbTemplate<Object, Object> rocksdbTemplate2 = RocksdbTemplateBuilder.builder(RocksdbTemplate.class, DefaultColumnFamilyOperations.class)
                .setDbName("test")   // 设置名称
                .setPath("rocksdb2")    // rocksdb目录
                .setDbOptionsConfigurer(dbOptionsConfigurer)  // rocksdb配置
                .setDefaultColumnFamilyOptionsConfigurer(defaultColumnFamilyConfigurer) // 默认cf配置
                .addColumnFamily("cf1", null, null)  // 增加cf1,使用默认配置
                .addColumnFamily("cf2", null, null)  // 增加cf2,使用默认配置
                .addColumnFamily("cf3", null, null)  // 增加cf3,使用默认配置
                .addColumnFamily("cf4", null, null)  // 增加cf4,使用默认配置
                .build();
        rocksdbTemplate2.put("k1", new HashMap<>());
        rocksdbTemplate2.put("k2", new ArrayList<>());
        System.out.println(rocksdbTemplate2.get("k1"));
        rocksdbTemplate2.getColumnFamilyOperations("cf1").put("k21", new ConcurrentHashMap<>());
        rocksdbTemplate2.getColumnFamilyOperations("cf1").put("k22", "v22");
        Object v21 = rocksdbTemplate2.getColumnFamilyOperations("cf1").get("k21");
        System.out.println(v21);
    }
}