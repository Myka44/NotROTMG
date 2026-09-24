package com.notrotmg.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public final class JacksonJsonCodec {
    private final ObjectMapper objectMapper;

    public JacksonJsonCodec() {
        this(new ObjectMapper());
    }

    public JacksonJsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public String encode(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new JsonCodecException("Could not serialize JSON", exception);
        }
    }

    public <T> T decode(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new JsonCodecException("Could not deserialize JSON as " + type.getSimpleName(), exception);
        }
    }
}
