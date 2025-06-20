# xctec-rocksdb
Helper lib that simplifies Rocksdb data access code.

## 前言
反馈交流：
QQ群：346994239
## 开始使用
### 1. 基于spring-boot


### 2. 普通模式
1. 添加依赖
```xml
    <dependency>
        <groupId>io.github.xctec</groupId>
        <artifactId>rocksdb-spring</artifactId>
        <version>${xctec-rocksdb.version}</version>
    </dependency>
```

2. 使用字符串作为序列化
``` java
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
```
3. 使用通用对象模式进行序列化
``` java
        DBOptionsConfigurer dbOptionsConfigurer = x -> x.setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
        ColumnFamilyConfigurer defaultColumnFamilyConfigurer = new  ColumnFamilyConfigurer("default", x1 -> {}, x2-> {
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
```

## 序列化说明
### 1. 通用模式
1. 值为字符串，可以直接使用字符串的实现
2. 值为对象，可以通过使用通用的值序列化器，来进行通用操作, 默认提供两种通用的序列化器
    - RocksdbSerializer.java()
    - RocksdbSerializer.json()
前者使用jdk序列化方式，后者使用jackson。两种方式都在序列化的值中加入了类型信息，从而在反序列化时，先读取类型进行，再通过类型信息对进行反序列化。

### 2. 非通用模式
通过addColumnFamily添加cf时，可以通过ColumnFamilyConfigurer来配置值序列化器，参考配置默认cf的代码

获取ColumnFamily时，需要自己处理识别泛型

## 开发说明
1.  修改版本
``` bash 
# 修改版本
mvn versions:set -DnewVersion=1.0.0
# 提交版本
mvn versions:commit
# 回滚修改
mvn versions:revert
```



