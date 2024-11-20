package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigPublisherSettings;

public interface WeatherPublisher {
    ConfigPublisherSettings PUBLISHER_SETTINGS = ConfigProvider.getInstance().loadConfig("publisher", ConfigPublisherSettings.class);

    void startScheduledTask(GeographicalLocation location);

    void publishWeatherDataForecast(WeatherDataForecast data);

    void stopScheduledTask();
}
