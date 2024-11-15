package it.lorenzoangelino.aircrowd.weather.config;

import it.lorenzoangelino.aircrowd.weather.mapper.Mapper;
import it.lorenzoangelino.aircrowd.weather.utils.FileUtils;

import java.io.File;

public class ConfigProviderImpl implements ConfigProvider {
    private final static String PARENT_CONFIG_DIR = "configs";
    private final static String CONFIGS_EXTENSION = "json";

    @Override
    public <T> T loadConfig(String name, Class<T> clazz) {
        File file = FileUtils.getFile(PARENT_CONFIG_DIR, name, CONFIGS_EXTENSION);
        return Mapper.fromJson(file, clazz);
    }
}
