package io.github.xctec.rocksdb.builder;

import org.rocksdb.DBOptions;

@FunctionalInterface
public interface DBOptionsConfigurer {
    void configure(DBOptions dbOptions);
}
