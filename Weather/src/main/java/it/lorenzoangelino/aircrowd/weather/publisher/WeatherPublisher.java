package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigPublisherSettings;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;

public interface WeatherPublisher {
    ConfigPublisherSettings PUBLISHER_SETTINGS = ConfigProvider.getInstance().loadConfig("publisher", ConfigPublisherSettings.class);

    void startScheduledTask(WeatherLocation location);

    void publishWeatherDataForecast(WeatherDataForecast data);

    void stopScheduledTask();
}
