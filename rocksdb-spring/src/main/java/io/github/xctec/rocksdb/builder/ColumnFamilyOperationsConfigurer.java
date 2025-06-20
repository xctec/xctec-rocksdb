package io.github.xctec.rocksdb.builder;

import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;

@FunctionalInterface
public interface ColumnFamilyOperationsConfigurer {
    void configure(AbstractColumnFamilyOperations columnFamilyOperations);
}
