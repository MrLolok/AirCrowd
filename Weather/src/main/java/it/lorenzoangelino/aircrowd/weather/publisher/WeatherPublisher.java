package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.weather.config.ConfigProvider;
import it.lorenzoangelino.aircrowd.weather.config.defaults.ConfigPublisherSettings;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;

public interface WeatherPublisher {
    ConfigPublisherSettings PUBLISHER_SETTINGS = ConfigProvider.getInstance().loadConfig("publisher", ConfigPublisherSettings.class);

    void startScheduledTask(WeatherLocation location);

    void publishWeatherDataForecast(WeatherDataForecast data);

    void stopScheduledTask();
}
