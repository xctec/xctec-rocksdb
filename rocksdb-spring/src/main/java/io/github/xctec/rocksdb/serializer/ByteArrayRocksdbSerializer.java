package io.github.xctec.rocksdb.serializer;

/**
 * 字节数组序列化器
 */
public class ByteArrayRocksdbSerializer implements RocksdbSerializer<byte[]> {
    @Override
    public byte[] serialize(byte[] value) {
        return value;
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        return bytes;
    }
}
