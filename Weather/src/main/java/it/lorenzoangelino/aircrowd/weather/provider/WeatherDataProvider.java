package it.lorenzoangelino.aircrowd.weather.provider;

import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherDataProvider {

    CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(GeographicalLocation location);

    CompletableFuture<WeatherData> fetchWeatherData(GeographicalLocation location, LocalDateTime date);
}
