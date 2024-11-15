package it.lorenzoangelino.aircrowd.weather.config;

public interface ConfigProvider {
    ConfigProvider INSTANCE = new ConfigProviderImpl();

    static ConfigProvider getInstance() {
        return INSTANCE;
    }

    <T> T loadConfig(String name, Class<T> clazz);
}
