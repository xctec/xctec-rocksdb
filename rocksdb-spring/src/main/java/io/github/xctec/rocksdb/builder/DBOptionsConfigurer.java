package io.github.xctec.rocksdb.builder;

import org.rocksdb.DBOptions;

/**
 * db选项配置接口
 */
@FunctionalInterface
public interface DBOptionsConfigurer {
    void configure(DBOptions dbOptions);
}
