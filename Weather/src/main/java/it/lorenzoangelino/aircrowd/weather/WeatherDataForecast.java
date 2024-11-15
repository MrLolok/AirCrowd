package it.lorenzoangelino.aircrowd.weather;

import java.util.List;

public record WeatherDataForecast(List<WeatherData> hourlyWeatherData) {
}
