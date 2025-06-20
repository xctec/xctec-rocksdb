package io.github.xctec.rocksdb.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@FunctionalInterface
public interface JacksonObjectReader {
    Object read(ObjectMapper mapper, byte[] source, JavaType type) throws IOException;

    static JacksonObjectReader create() {
        return (mapper, source, type) -> mapper.readValue(source, 0, source.length, type);
    }
}
