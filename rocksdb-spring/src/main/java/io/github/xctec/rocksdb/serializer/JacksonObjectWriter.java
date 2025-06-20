package io.github.xctec.rocksdb.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@FunctionalInterface
public interface JacksonObjectWriter {
    byte[] write(ObjectMapper mapper, Object source) throws IOException;
    static JacksonObjectWriter create() {
        return ObjectMapper::writeValueAsBytes;
    }
}
