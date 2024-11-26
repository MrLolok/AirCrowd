package it.lorenzoangelino.aircrowd.common.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public interface Mapper {
    Logger LOGGER = LogManager.getLogger(Mapper.class);
    ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to translate %s and transform it in {}.", json, e);
            return null;
        }
    }

    static <T> T fromJson(File file, Class<T> clazz) {
        try {
            return DEFAULT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            LOGGER.error("Unable to translate file {} and transform it in {}.", file.getName(), clazz.getSimpleName(), e);
            return null;
        }
    }

    static String toJson(Object src) {
        try {
            return DEFAULT_MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to transform {} in a JSON string.", src.getClass().getSimpleName(), e);
            return null;
        }
    }
}
