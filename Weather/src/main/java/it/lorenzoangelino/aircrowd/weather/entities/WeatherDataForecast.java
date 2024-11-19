package it.lorenzoangelino.aircrowd.weather.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record WeatherDataForecast(@NotNull List<WeatherData> hourlyWeatherData) {
}
