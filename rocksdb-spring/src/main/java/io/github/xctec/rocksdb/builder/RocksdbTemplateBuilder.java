package io.github.xctec.rocksdb.builder;

import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;
import io.github.xctec.rocksdb.core.RocksdbTemplate;
import io.github.xctec.rocksdb.exception.BaseException;
import org.rocksdb.*;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RocksdbTemplateBuilder<T extends RocksdbTemplate, CF extends AbstractColumnFamilyOperations> {

    private Class<T> rocksdbTemplateClass;

    private Class<CF> columnFamilyOperationsClass;

    private String dbName;

    private String path;

    private boolean createIfMissing = true;

    private boolean createMissingColumnFamilies = true;

    private boolean enableStatistics = true;

    private DBOptionsConfigurer dbOptionsConfigurer;

    private ColumnFamilyConfigurer defaultColumnFamilyConfigurer = new ColumnFamilyConfigurer("default");

    private Map<String, ColumnFamilyConfigurer> columnFamilyConfigurerMap = new HashMap<>();

    public RocksdbTemplateBuilder(Class<T> rocksdbTemplateClass, Class<CF> columnFamilyOperationsClass) {
        this.rocksdbTemplateClass = rocksdbTemplateClass;
        this.columnFamilyOperationsClass = columnFamilyOperationsClass;
    }

    public RocksdbTemplateBuilder(String dbName, String path) {
        this.path = path;
    }

    public static <T extends RocksdbTemplate, CF extends AbstractColumnFamilyOperations> RocksdbTemplateBuilder<T, CF>
    builder(Class<T> rocksdbTemplateClass, Class<CF> columnFamilyOperationsClass) {
        return new RocksdbTemplateBuilder(rocksdbTemplateClass, columnFamilyOperationsClass);
    }

    public RocksdbTemplateBuilder<T, CF> setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setPath(String path) {
        this.path = path;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setCreateIfMissing(boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setCreateMissingColumnFamilies(boolean createMissingColumnFamilies) {
        this.createMissingColumnFamilies = createMissingColumnFamilies;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setDbOptionsConfigurer(DBOptionsConfigurer dbOptionsConfigurer) {
        this.dbOptionsConfigurer = dbOptionsConfigurer;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setDefaultColumnFamilyOptionsConfigurer(ColumnFamilyOptionsConfigurer configurer,
                                                                                 ColumnFamilyOperationsConfigurer operationsConfigurer) {
        this.defaultColumnFamilyConfigurer = new ColumnFamilyConfigurer("default", configurer, operationsConfigurer);
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setDefaultColumnFamilyOptionsConfigurer(ColumnFamilyConfigurer defaultColumnFamilyConfigurer) {
        if (defaultColumnFamilyConfigurer != null) {
            this.defaultColumnFamilyConfigurer = defaultColumnFamilyConfigurer;
        }
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> addColumnFamily(String cfName) {
        this.columnFamilyConfigurerMap.put(cfName, new ColumnFamilyConfigurer(cfName));
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> addColumnFamily(String cfName, ColumnFamilyOptionsConfigurer configurer,
                                                         ColumnFamilyOperationsConfigurer operationsConfigurer) {
        this.columnFamilyConfigurerMap.put(cfName, new ColumnFamilyConfigurer(cfName, configurer, operationsConfigurer));
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> addColumnFamily(String cfName, ColumnFamilyConfigurer configurer) {
        if (configurer != null) {
            this.columnFamilyConfigurerMap.put(cfName, configurer);
        }
        return this;
    }

    public T build() {
        try {
            Assert.hasText(dbName, "dbName must not be empty");
            Assert.hasText(path, "path must not be empty");
            DBOptions dbOptions = new DBOptions();
            if (dbOptionsConfigurer != null) {
                this.dbOptionsConfigurer.configure(dbOptions);
            }
            dbOptions.setCreateIfMissing(createIfMissing)
                    .setCreateMissingColumnFamilies(createMissingColumnFamilies);
            Statistics statistics = null;
            if (enableStatistics) {
                dbOptions.setStatistics(statistics = new Statistics());
            }
            List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

            // default column family
            ColumnFamilyOptions defaultColumnFamilyOptions = new ColumnFamilyOptions();
            defaultColumnFamilyConfigurer.getConfigurer().configure(defaultColumnFamilyOptions);
            ColumnFamilyDescriptor defaultColumnFamilyDescriptor = new ColumnFamilyDescriptor("default".getBytes(), defaultColumnFamilyOptions);
            descriptors.add(defaultColumnFamilyDescriptor);

            for (String key : columnFamilyConfigurerMap.keySet()) {
                ColumnFamilyOptions columnFamilyOptions = new ColumnFamilyOptions();
                // 先应用默认配置
                defaultColumnFamilyConfigurer.getConfigurer().configure(columnFamilyOptions);
                // 如果有单独配置，应用单独配置
                ColumnFamilyConfigurer configurer = columnFamilyConfigurerMap.get(key);
                if (configurer != null) {
                    configurer.getConfigurer().configure(columnFamilyOptions);
                }
                ColumnFamilyDescriptor columnFamilyDescriptor = new ColumnFamilyDescriptor(key.getBytes(), columnFamilyOptions);
                descriptors.add(columnFamilyDescriptor);
            }
            List<ColumnFamilyHandle> handles = new ArrayList<>();
            RocksDB db = RocksDB.open(dbOptions, this.path, descriptors, handles);

            Constructor<T> constructor = rocksdbTemplateClass.getConstructor();
            T rocksdbTemplate = constructor.newInstance();
            rocksdbTemplate.setDb(db);
            rocksdbTemplate.setDbName(dbName);
            rocksdbTemplate.setStatistics(statistics);

            for (ColumnFamilyHandle columnFamilyHandle : handles) {
                String cfName = new String(columnFamilyHandle.getName());
                Constructor<CF> cfConstructor = columnFamilyOperationsClass.getConstructor();

                CF columnFamilyOperations = cfConstructor.newInstance();
                columnFamilyOperations.setDbName(dbName);
                columnFamilyOperations.setDb(db);
                columnFamilyOperations.setCfName(cfName);
                columnFamilyOperations.setColumnFamilyHandle(columnFamilyHandle);
                // 先应用默认的配置
                defaultColumnFamilyConfigurer.getOperationsConfigurer().configure(columnFamilyOperations);
                if (columnFamilyConfigurerMap.containsKey(cfName)) {
                    columnFamilyConfigurerMap.get(cfName).getOperationsConfigurer().configure(columnFamilyOperations);
                }
                rocksdbTemplate.setColumnFamilyOperations(cfName, columnFamilyOperations);
            }
            return (T) rocksdbTemplate;
        } catch (Exception e) {
            throw new BaseException("build rocksdbTemplate fail.", e);
        }
    }
}
