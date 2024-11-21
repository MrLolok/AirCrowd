package it.lorenzoangelino.aircrowd.weather.provider;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigResponsesSettings;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherDataProvider {
    ConfigResponsesSettings RESPONSES_SETTINGS = ConfigProvider.getInstance().loadConfig("responses", ConfigResponsesSettings.class);

    CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(GeographicalLocation location);

    CompletableFuture<WeatherData> fetchWeatherData(GeographicalLocation location, LocalDateTime date);
}
