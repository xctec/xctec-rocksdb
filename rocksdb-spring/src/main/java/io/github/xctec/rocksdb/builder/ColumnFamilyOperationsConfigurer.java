package io.github.xctec.rocksdb.builder;

import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;

/**
 * cf操作配置接口
 */
@FunctionalInterface
public interface ColumnFamilyOperationsConfigurer {
    void configure(AbstractColumnFamilyOperations columnFamilyOperations);
}
