package io.github.xctec.rocksdb.serializer;

public class ByteArrayRocksdbSerializer implements RocksdbSerializer<byte[]>{
    @Override
    public byte[] serialize(byte[] value) {
        return value;
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        return bytes;
    }
}
