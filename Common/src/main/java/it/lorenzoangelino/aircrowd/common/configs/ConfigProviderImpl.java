package it.lorenzoangelino.aircrowd.common.configs;

import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.utils.FileUtils;
import java.io.File;

public class ConfigProviderImpl implements ConfigProvider {
    private static final String PARENT_CONFIG_DIR = "configs";
    private static final String CONFIGS_EXTENSION = "json";

    @Override
    public <T> T loadConfig(String name, Class<T> clazz) {
        File file = FileUtils.getFile(PARENT_CONFIG_DIR, name, CONFIGS_EXTENSION);
        return Mapper.fromJson(file, clazz);
    }
}
