package it.lorenzoangelino.aircrowd.weather.services.weather;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigOpenMeteoAPISettings;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherData;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface WeatherService {
    ConfigOpenMeteoAPISettings OPEN_METEO_API_SETTINGS = ConfigProvider.getInstance().loadConfig("open-meteo", ConfigOpenMeteoAPISettings.class);

    WeatherDataProvider getWeatherDataProvider();

    CompletableFuture<WeatherDataForecast> getWeatherForecast(WeatherLocation location);

    CompletableFuture<WeatherData> getCurrentWeather(WeatherLocation location);

    CompletableFuture<WeatherData> getHourlyWeather(WeatherLocation location, LocalDateTime date);
}
