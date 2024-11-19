package it.lorenzoangelino.aircrowd.common.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public interface Mapper {
    ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to translate %s and transform it in %s.", json, clazz.getSimpleName()), e);
        }
    }

    static <T> T fromJson(File file, Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to translate file %s and transform it in %s.", file.getName(), clazz.getSimpleName()), e);
        }
    }

    static String toJson(Object src) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Unable to transform %s in a JSON string.", src.getClass().getSimpleName()), e);
        }
    }
}
