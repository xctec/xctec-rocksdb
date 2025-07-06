package io.github.xctec.rocksdb.logger;

import org.rocksdb.InfoLogLevel;
import org.rocksdb.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用slf4j输出的日志记录器
 */
public class Slf4jLogger extends Logger {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(Slf4jLogger.class);

    private String dbName = "default";

    public Slf4jLogger(InfoLogLevel logLevel) {
        super(logLevel);
    }

    public Slf4jLogger(InfoLogLevel logLevel, String dbName) {
        super(logLevel);
        this.dbName = dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    @Override
    protected void log(InfoLogLevel logLevel, String message) {
        switch (logLevel) {
            case FATAL_LEVEL:
            case ERROR_LEVEL:
                logger.error("[{}]{}", dbName, message);
                break;
            case WARN_LEVEL:
                logger.warn("[{}]{}", dbName, message);
                break;
            case INFO_LEVEL:
                logger.info("[{}]{}", dbName, message);
                break;
            case DEBUG_LEVEL:
                logger.debug("[{}]{}", dbName, message);
                break;
            default:
                logger.trace("[{}]{}", dbName, message);
                break;
        }
    }
}
