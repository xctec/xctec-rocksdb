package io.github.xctec.rocksdb.builder;

import org.rocksdb.ColumnFamilyOptions;

@FunctionalInterface
public interface ColumnFamilyOptionsConfigurer {
    void configure(ColumnFamilyOptions columnFamilyOptions);
}
