package io.github.xctec.rocksdb.boot.configure;

import io.github.xctec.rocksdb.builder.ColumnFamilyConfigurer;
import io.github.xctec.rocksdb.builder.DBOptionsConfigurer;
import io.github.xctec.rocksdb.builder.RocksdbTemplateBuilder;
import io.github.xctec.rocksdb.core.AbstractColumnFamilyOperations;
import io.github.xctec.rocksdb.core.RocksdbTemplate;
import io.github.xctec.rocksdb.logger.Slf4jLogger;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.RocksDB;
import org.rocksdb.util.StdErrLogger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AutoConfiguration
@ConditionalOnClass(RocksDB.class)
@EnableConfigurationProperties(RocksdbProperties.class)
public class RocksdbAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "rocksdbTemplate")
    public RocksdbTemplate rocksdbTemplate(RocksdbProperties properties,
                                           ObjectProvider<DBOptionsConfigurer> dbOptionsConfigurerProvider,
                                           List<ColumnFamilyConfigurer> columnFamilyConfigurers) {
        RocksDB.loadLibrary();
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
        LoggerInterface logger = buildLogger(properties);
        if (logger != null) {
            builder.setLogger(logger);
        }
        if (columnFamilyConfigurers != null && !columnFamilyConfigurers.isEmpty()) {
            for (ColumnFamilyConfigurer columnFamilyConfigurer : columnFamilyConfigurers) {
                builder.addColumnFamily(columnFamilyConfigurer.getCfName(), columnFamilyConfigurer);
            }
        }
        return builder.build();
    }

    public LoggerInterface buildLogger(RocksdbProperties rocksdbProperties) {
        Map<String, InfoLogLevel> map = new HashMap<>();
        map.put("debug", InfoLogLevel.DEBUG_LEVEL);
        map.put("info", InfoLogLevel.INFO_LEVEL);
        map.put("warn", InfoLogLevel.WARN_LEVEL);
        map.put("error", InfoLogLevel.ERROR_LEVEL);
        map.put("fatal", InfoLogLevel.FATAL_LEVEL);
        String loggerLevel = rocksdbProperties.getLoggerLevel();
        InfoLogLevel infoLogLevel = map.get(loggerLevel);
        if (infoLogLevel == null) {
            infoLogLevel = InfoLogLevel.ERROR_LEVEL;
        }
        LoggerInterface loggerInterface = null;
        String loggerType = rocksdbProperties.getLoggerType();
        if ("slf4j".equals(loggerType)) {
            loggerInterface = new Slf4jLogger(infoLogLevel, rocksdbProperties.getDbName());
        } else if ("std".equals(loggerType)) {
            // 添加一个空格，方便阅读
            String logPrefix = "[" + rocksdbProperties.getDbName() + "] ";
            loggerInterface = new StdErrLogger(infoLogLevel, logPrefix);
        } else {
            // 相当于loggerType = "none"
        }
        return loggerInterface;
    }
}
