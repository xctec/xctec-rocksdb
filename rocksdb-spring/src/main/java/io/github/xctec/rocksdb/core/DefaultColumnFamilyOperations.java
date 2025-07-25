package io.github.xctec.rocksdb.core;

import io.github.xctec.rocksdb.exception.BaseException;
import org.rocksdb.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * ColumnFamilyOperations的默认实现
 *
 * @param <K>
 * @param <V>
 */
public class DefaultColumnFamilyOperations<K, V> extends AbstractColumnFamilyOperations<K, V> {

    public DefaultColumnFamilyOperations() {

    }

    public DefaultColumnFamilyOperations(String dbName, RocksDB db, String cfName, ColumnFamilyHandle columnFamilyHandle) {
        super(dbName, db, cfName, columnFamilyHandle);
    }

    @Override
    public RocksIterator newIterator(ReadOptions readOptions) {
        return getDb().newIterator(getColumnFamilyHandle(), readOptions);
    }

    @Override
    public V get(ReadOptions readOptions, K key) {
        try {
            byte[] rawKey = rawKey(key);
            byte[] rawValue = getDb().get(getColumnFamilyHandle(), readOptions, rawKey);
            return deserializeValue(rawValue);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void put(WriteOptions writeOptions, K key, V value) {
        try {
            byte[] rawValue = rawValue(value);
            byte[] rawKey = rawKey(key);
            getDb().put(getColumnFamilyHandle(), writeOptions, rawKey, rawValue);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void merge(WriteOptions writeOptions, byte[] key, byte[] value) {
        try {
            getDb().merge(getColumnFamilyHandle(), writeOptions, key, value);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void merge(WriteOptions writeOptions, K key, V value) {
        try {
            byte[] rawValue = rawValue(value);
            byte[] rawKey = rawKey(key);
            getDb().merge(getColumnFamilyHandle(), writeOptions, rawKey, rawValue);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void batch(WriteOptions writeOptions, Consumer<WriteBatch> writeBatchConsumer) {
        RocksDB db = getDb();
        ColumnFamilyHandle columnFamilyHandle = getColumnFamilyHandle();
        try (WriteBatch writeBatch = new WriteBatch()) {
            writeBatchConsumer.accept(writeBatch);
            db.write(writeOptions, writeBatch);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void batchPut(WriteOptions writeOptions, List<K> keys, List<V> values) {
        RocksDB db = getDb();
        ColumnFamilyHandle columnFamilyHandle = getColumnFamilyHandle();
        try (WriteBatch writeBatch = new WriteBatch()) {
            for (int i = 0; i < keys.size(); i++) {
                byte[] rawKey = rawKey(keys.get(i));
                byte[] rawValue = rawValue(values.get(i));
                writeBatch.put(columnFamilyHandle, rawKey, rawValue);
            }
            db.write(writeOptions, writeBatch);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void delete(WriteOptions writeOptions, K key) {
        try {
            byte[] rawKey = rawKey(key);
            getDb().delete(getColumnFamilyHandle(), writeOptions, rawKey);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void singleDelete(WriteOptions writeOptions, K key) {
        try {
            byte[] rawKey = rawKey(key);
            getDb().singleDelete(getColumnFamilyHandle(), writeOptions, rawKey);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void deleteRange(WriteOptions writeOptions, K startKey, K endKey) {
        try {
            byte[] startRawKey = rawKey(startKey);
            byte[] endRawKey = rawKey(endKey);
            getDb().deleteRange(getColumnFamilyHandle(), writeOptions, startRawKey, endRawKey);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public boolean exists(K key) {
        try {
            byte[] rawKey = rawKey(key);
            return getDb().keyExists(getColumnFamilyHandle(), rawKey);
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void flush(boolean waitForFlush, boolean allowWriteStall) {
        try (FlushOptions flushOptions = new FlushOptions()) {
            flushOptions.setWaitForFlush(waitForFlush);
            flushOptions.setAllowWriteStall(allowWriteStall);
            getDb().flush(flushOptions, getColumnFamilyHandle());
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    @Override
    public void iterator(ReadOptions readOptions, String startType, String seekKey, String order, IteratorCallback<K, V> iteratorCallback) {
        iterator(readOptions, startType, seekKey, order, 1, Long.MAX_VALUE, iteratorCallback);
    }

    @Override
    public void iterator(ReadOptions readOptions, String startType, String seekKey, String order, long offset, long count, IteratorCallback<K, V> iteratorCallback) {
        if (offset < 1) {
            offset = 1;
        }
        try (RocksIterator iterator = getDb().newIterator(this.getColumnFamilyHandle(), readOptions)) {
            if ("first".equals(startType)) {
                iterator.seekToFirst();
            } else if ("last".equals(startType)) {
                iterator.seekToLast();
            } else if ("key".equals(startType) && StringUtils.hasText(seekKey)) {
                if ("prev".equals(order)) {
                    iterator.seekForPrev(seekKey.getBytes());
                } else {
                    iterator.seek(seekKey.getBytes());
                }
            } else {

            }
            long curIndex = 1;
            long curCount = 0;
            while (iterator.isValid()) {
                // 跳过offset，才进行回调
                if (curIndex++ >= offset) {
                    K key = deserializeKey(iterator.key());
                    V value = deserializeValue(iterator.value());
                    iteratorCallback.callback(key, value, curIndex);
                }
                // 获取count条
                if (++curCount >= count) {
                    break;
                }
                if ("prev".equals(order)) {
                    iterator.prev();
                } else {
                    iterator.next();
                }
            }
            try {
                iterator.status();
            } catch (RocksDBException e) {
                throw new BaseException(e);
            }
        }
    }

    @Override
    public void close() {
        getColumnFamilyHandle().close();
    }
}
