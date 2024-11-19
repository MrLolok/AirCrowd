package it.lorenzoangelino.aircrowd.weather.provider;

import it.lorenzoangelino.aircrowd.weather.entities.WeatherData;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherDataProvider {
    CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(WeatherLocation location);

    CompletableFuture<WeatherData> fetchWeatherData(WeatherLocation location, LocalDateTime date);
}
