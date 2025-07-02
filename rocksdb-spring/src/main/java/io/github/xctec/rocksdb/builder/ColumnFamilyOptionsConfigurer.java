package io.github.xctec.rocksdb.builder;

import org.rocksdb.ColumnFamilyOptions;

/**
 * cf选项配置接口
 */
@FunctionalInterface
public interface ColumnFamilyOptionsConfigurer {
    void configure(ColumnFamilyOptions columnFamilyOptions);
}
