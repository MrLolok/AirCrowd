package it.lorenzoangelino.aircrowd.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigProviderImpl implements ConfigProvider {
    private static final String PARENT_CONFIG_DIR = "configs";
    private static final String CONFIGS_EXTENSION = "json";
    
    private final ObjectMapper objectMapper;

    @Override
    public <T> T loadConfig(String name, Class<T> clazz) {
        try {
            File file = FileUtils.getFile(PARENT_CONFIG_DIR, name, CONFIGS_EXTENSION);
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error("Failed to load config file: {}.{}", name, CONFIGS_EXTENSION, e);
            return null;
        }
    }
}
