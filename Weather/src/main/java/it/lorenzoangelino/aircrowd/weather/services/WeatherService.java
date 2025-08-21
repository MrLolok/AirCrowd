package it.lorenzoangelino.aircrowd.weather.services;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigOpenMeteoAPISettings;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherService {
    ConfigOpenMeteoAPISettings OPEN_METEO_API_SETTINGS =
            ConfigProvider.getInstance().loadConfig("open-meteo", ConfigOpenMeteoAPISettings.class);

    WeatherDataProvider getWeatherDataProvider();

    void startAutomaticPublishing(GeographicalLocation location);

    void stopAutomaticPublishing();

    CompletableFuture<WeatherDataForecast> getWeatherForecast(GeographicalLocation location);

    CompletableFuture<WeatherData> getCurrentWeather(GeographicalLocation location);

    CompletableFuture<WeatherData> getHourlyWeather(GeographicalLocation location, LocalDateTime date);
}
