package io.github.xctec.rocksdb.serializer;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringRocksdbSerializer implements RocksdbSerializer<String> {

    private final Charset charset;

    public static final StringRocksdbSerializer US_ASCII = new StringRocksdbSerializer(StandardCharsets.US_ASCII);

    public static final StringRocksdbSerializer ISO_8859_1 = new StringRocksdbSerializer(StandardCharsets.ISO_8859_1);

    public static final StringRocksdbSerializer UTF_8 = new StringRocksdbSerializer(StandardCharsets.UTF_8);

    public StringRocksdbSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public StringRocksdbSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String value) {
        return (value == null ? null : value.getBytes(charset));
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }

    @Override
    public Class<?> getTargetType() {
        return String.class;
    }
}
