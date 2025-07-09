package io.github.xctec.rocksdb.core;

import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认时间监听器， 用于打印事件触发信息
 */
public class DefaultEventListener extends AbstractEventListener {

    private Logger logger = LoggerFactory.getLogger(DefaultEventListener.class);

    private String dbName;

    public DefaultEventListener(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void onFlushCompleted(RocksDB db, FlushJobInfo flushJobInfo) {
        logger.info("[rocksdb][onFlushCompleted][{}]{}", dbName, flushJobInfo);
    }

    @Override
    public void onFlushBegin(RocksDB db, FlushJobInfo flushJobInfo) {
        logger.info("[rocksdb][onFlushBegin][{}]{}", dbName, flushJobInfo);
    }

    @Override
    public void onTableFileDeleted(TableFileDeletionInfo tableFileDeletionInfo) {
        logger.info("[rocksdb][onTableFileDeleted][{}]{}", dbName, tableFileDeletionInfo);
    }

    @Override
    public void onCompactionBegin(RocksDB db, CompactionJobInfo compactionJobInfo) {
        logger.info("[rocksdb][onCompactionBegin][{}]{}", dbName, strCompactionJobInfo(compactionJobInfo));
    }

    @Override
    public void onCompactionCompleted(RocksDB db, CompactionJobInfo compactionJobInfo) {
        logger.info("[rocksdb][onCompactionCompleted][{}]{}", dbName, strCompactionJobInfo(compactionJobInfo));
    }

    @Override
    public void onTableFileCreated(TableFileCreationInfo tableFileCreationInfo) {
        logger.info("[rocksdb][onTableFileCreated][{}]{}", dbName, tableFileCreationInfo);
    }

    @Override
    public void onTableFileCreationStarted(TableFileCreationBriefInfo tableFileCreationBriefInfo) {
        logger.info("[rocksdb][onTableFileCreationStarted][{}]{}", dbName, tableFileCreationBriefInfo);
    }

    @Override
    public void onMemTableSealed(MemTableInfo memTableInfo) {
        logger.info("[rocksdb][onMemTableSealed][{}]{}", dbName, memTableInfo);
    }

    @Override
    public void onColumnFamilyHandleDeletionStarted(ColumnFamilyHandle columnFamilyHandle) {
        logger.info("[rocksdb][onColumnFamilyHandleDeletionStarted][{}]{}", dbName, strColumnFamilyHandle(columnFamilyHandle));
    }

    @Override
    public void onExternalFileIngested(RocksDB db, ExternalFileIngestionInfo externalFileIngestionInfo) {
        logger.info("[rocksdb][onExternalFileIngested][{}]{}", dbName, externalFileIngestionInfo);
    }

    @Override
    public void onBackgroundError(BackgroundErrorReason backgroundErrorReason, Status backgroundError) {
        logger.info("[rocksdb][onBackgroundError][{}]{}", dbName, strStatus(backgroundError));
    }

    @Override
    public void onStallConditionsChanged(WriteStallInfo writeStallInfo) {
        logger.info("[rocksdb][onStallConditionsChanged][{}]{}", dbName, writeStallInfo);
    }

    @Override
    public void onFileReadFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[rocksdb][onFileReadFinish][{}]{}", dbName, fileOperationInfo);
    }

    @Override
    public void onFileWriteFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[rocksdb][onFileWriteFinish][{}]{}", dbName, fileOperationInfo);
    }

    @Override
    public void onFileFlushFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[rocksdb][onFileFlushFinish][{}]{}", dbName, fileOperationInfo);
    }

    @Override
    public void onFileSyncFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[rocksdb][onFileSyncFinish][{}]{}", dbName, fileOperationInfo);
    }

    @Override
    public void onFileRangeSyncFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[rocksdb][onFileRangeSyncFinish][{}]{}", dbName, fileOperationInfo);
    }

    @Override
    public void onFileTruncateFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[onFileTruncateFinish] info: {}", fileOperationInfo);
    }

    @Override
    public void onFileCloseFinish(FileOperationInfo fileOperationInfo) {
        logger.info("[onFileCloseFinish] info: {}", fileOperationInfo);
    }

    @Override
    public void onErrorRecoveryCompleted(Status oldBackgroundError) {
        logger.info("[onErrorRecoveryCompleted] info: {}", strStatus(oldBackgroundError));
    }

    private Object strColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle) {
        try {
            return String.format("ColumnFamilyHandle[name: %s]", new String(columnFamilyHandle.getName()));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    private String strCompactionJobInfo(CompactionJobInfo compactionJobInfo) {
        return String.format("CompactionJobInfo[cfName: %s, jobId: %s, threadId: %s, status: %s]",
                new String(compactionJobInfo.columnFamilyName()),
                compactionJobInfo.jobId(),
                compactionJobInfo.threadId(),
                strStatus(compactionJobInfo.status()));
    }

    private String strStatus(Status status) {
        return String.format("status[code: %d, subCode: %d, state: %s]", status.getCode(), status.getSubCode(), status.getState());
    }
}
