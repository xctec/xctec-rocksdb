package io.github.xctec.rocksdb.builder;

import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;
import io.github.xctec.rocksdb.core.DefaultEventListener;
import io.github.xctec.rocksdb.core.RocksdbTemplate;
import io.github.xctec.rocksdb.exception.BaseException;
import org.rocksdb.*;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RocksdbTemplate的构建器
 *
 * @param <T>
 * @param <CF>
 */
public class RocksdbTemplateBuilder<T extends RocksdbTemplate, CF extends AbstractColumnFamilyOperations> {

    static {
        // 提前加载动态库
        RocksDB.loadLibrary();
    }

    private Class<T> rocksdbTemplateClass;

    private Class<CF> columnFamilyOperationsClass;

    private String dbName;

    private String path;

    private boolean createIfMissing = true;

    private boolean createMissingColumnFamilies = true;

    private boolean enableStatistics = true;

    private boolean enableDefaultEventListener = false;

    private LoggerInterface logger;

    private DBOptionsConfigurer dbOptionsConfigurer;

    private ColumnFamilyConfigurer defaultColumnFamilyConfigurer = new ColumnFamilyConfigurer("default");

    private Map<String, ColumnFamilyConfigurer> columnFamilyConfigurerMap = new HashMap<>();

    private List<AbstractEventListener> eventListeners = new ArrayList<>();

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

    public RocksdbTemplateBuilder<T, CF> setEnableDefaultEventListener(boolean enableDefaultEventListener) {
        this.enableDefaultEventListener = enableDefaultEventListener;
        return this;
    }

    public RocksdbTemplateBuilder<T, CF> setLogger(LoggerInterface logger) {
        this.logger = logger;
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

    public RocksdbTemplateBuilder<T, CF> addEventListener(AbstractEventListener eventListener) {
        this.eventListeners.add(eventListener);
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
            if (logger != null) {
                dbOptions.setLogger(logger);
            }
            List<AbstractEventListener> listeners = new ArrayList<>();
            if (enableDefaultEventListener) {
                listeners.add(new DefaultEventListener(dbName));
            }
            listeners.addAll(this.eventListeners);
            dbOptions.setListeners(listeners);
            List<ColumnFamilyDescriptor> descriptors = new ArrayList<>();

            // default column family
            ColumnFamilyOptions defaultColumnFamilyOptions = new ColumnFamilyOptions();
            defaultColumnFamilyConfigurer.getConfigurer().configure(defaultColumnFamilyOptions);
            ColumnFamilyDescriptor defaultColumnFamilyDescriptor = new ColumnFamilyDescriptor("default".getBytes(), defaultColumnFamilyOptions);
            descriptors.add(defaultColumnFamilyDescriptor);

            for (String key : columnFamilyConfigurerMap.keySet()) {
                if ("default".equals(key)) {
                    continue;
                }
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
