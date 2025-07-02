package io.github.xctec.rocksdb.builder;

/**
 * 默认cf配置类
 */
public class DefaultColumnFamilyConfigurer extends ColumnFamilyConfigurer {

    public DefaultColumnFamilyConfigurer() {
        super("default");
    }

    public DefaultColumnFamilyConfigurer(ColumnFamilyOptionsConfigurer configurer, ColumnFamilyOperationsConfigurer operationsConfigurer) {
        super("default", configurer, operationsConfigurer);
    }
}
