package io.github.xctec.rocksdb.boot.configure;

import io.github.xctec.rocksdb.builder.ColumnFamilyConfigurer;
import io.github.xctec.rocksdb.builder.DBOptionsConfigurer;
import io.github.xctec.rocksdb.builder.RocksdbTemplateBuilder;
import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;
import io.github.xctec.rocksdb.core.ColumnFamilyOperations;
import io.github.xctec.rocksdb.core.RocksdbTemplate;
import org.rocksdb.RocksDB;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;


@AutoConfiguration
@ConditionalOnClass(RocksDB.class)
@EnableConfigurationProperties(RocksdbProperties.class)
public class RocksdbAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "rocksdbTemplate")
    public RocksdbTemplate rocksdbTemplate(RocksdbProperties properties,
                                           ObjectProvider<DBOptionsConfigurer> dbOptionsConfigurerProvider,
                                           List<ColumnFamilyConfigurer> columnFamilyConfigurers, ColumnFamilyOperations columnFamilyOperations) {
        RocksdbTemplateBuilder<? extends RocksdbTemplate, ? extends AbstractColumnFamilyOperations> builder =
                RocksdbTemplateBuilder.builder(properties.getRocksdbTemplateClass(), properties.getColumnFamilyOperationsClass())
                        .setPath(properties.getPath())
                        .setDbName(properties.getDbName())
                        .setCreateIfMissing(properties.isCreateIfMissing())
                        .setCreateMissingColumnFamilies(properties.isCreateMissingColumnFamilies())
                        .setEnableStatistics(properties.isEnableStatistics());
        dbOptionsConfigurerProvider.ifAvailable(x -> {
            builder.setDbOptionsConfigurer(x);
        });
        if (columnFamilyConfigurers != null && !columnFamilyConfigurers.isEmpty()) {
            for (ColumnFamilyConfigurer columnFamilyConfigurer : columnFamilyConfigurers) {
                builder.addColumnFamily(columnFamilyConfigurer.getCfName(), columnFamilyConfigurer);
            }
        }
        return builder.build();
    }
}
