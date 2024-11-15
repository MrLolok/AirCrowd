package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.weather.locations.Location;

import java.util.concurrent.CompletableFuture;

public interface WeatherDataProvider {
    CompletableFuture<WeatherDataForecast> fetchWeatherData(Location location);

    CompletableFuture<WeatherData> fetchWeatherData(Location location, int hour);
}
