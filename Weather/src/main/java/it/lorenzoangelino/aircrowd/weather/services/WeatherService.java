package it.lorenzoangelino.aircrowd.weather.services;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherService {

    WeatherDataProvider getWeatherDataProvider();

    void startAutomaticPublishing(GeographicalLocation location);

    void stopAutomaticPublishing();

    CompletableFuture<WeatherDataForecast> getWeatherForecast(GeographicalLocation location);

    CompletableFuture<WeatherData> getCurrentWeather(GeographicalLocation location);

    CompletableFuture<WeatherData> getHourlyWeather(GeographicalLocation location, LocalDateTime date);
}
