package io.github.xctec.rocksdb.boot.configure;

import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;
import io.github.xctec.rocksdb.core.DefaultStringColumnFamilyOperations;
import io.github.xctec.rocksdb.core.RocksdbTemplate;
import io.github.xctec.rocksdb.core.StringRocksdbTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rocksdb")
public class RocksdbProperties {

    private Class<? extends RocksdbTemplate> rocksdbTemplateClass = StringRocksdbTemplate.class;

    private Class<? extends AbstractColumnFamilyOperations> columnFamilyOperationsClass = DefaultStringColumnFamilyOperations.class;

    private String dbName;

    private String path;

    private boolean createIfMissing = true;

    private boolean createMissingColumnFamilies = true;

    private boolean enableStatistics = true;

    private String loggerType = "std";

    private String loggerLevel = "error";

    private boolean enableDefaultEventListener = true;

    public Class<? extends RocksdbTemplate> getRocksdbTemplateClass() {
        return rocksdbTemplateClass;
    }

    public void setRocksdbTemplateClass(Class<? extends RocksdbTemplate> rocksdbTemplateClass) {
        this.rocksdbTemplateClass = rocksdbTemplateClass;
    }

    public Class<? extends AbstractColumnFamilyOperations> getColumnFamilyOperationsClass() {
        return columnFamilyOperationsClass;
    }

    public void setColumnFamilyOperationsClass(Class<? extends AbstractColumnFamilyOperations> columnFamilyOperationsClass) {
        this.columnFamilyOperationsClass = columnFamilyOperationsClass;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCreateIfMissing() {
        return createIfMissing;
    }

    public void setCreateIfMissing(boolean createIfMissing) {
        this.createIfMissing = createIfMissing;
    }

    public boolean isCreateMissingColumnFamilies() {
        return createMissingColumnFamilies;
    }

    public void setCreateMissingColumnFamilies(boolean createMissingColumnFamilies) {
        this.createMissingColumnFamilies = createMissingColumnFamilies;
    }

    public boolean isEnableStatistics() {
        return enableStatistics;
    }

    public void setEnableStatistics(boolean enableStatistics) {
        this.enableStatistics = enableStatistics;
    }

    public String getLoggerType() {
        return loggerType;
    }

    public void setLoggerType(String loggerType) {
        this.loggerType = loggerType;
    }

    public String getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(String loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

    public boolean isEnableDefaultEventListener() {
        return enableDefaultEventListener;
    }

    public void setEnableDefaultEventListener(boolean enableDefaultEventListener) {
        this.enableDefaultEventListener = enableDefaultEventListener;
    }
}
