package it.lorenzoangelino.aircrowd.airportmanagement.clients;

import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather-service", url = "${aircrowd.services.weather.url:http://weather-service}")
public interface WeatherServiceClient {

    @GetMapping("/api/v1/weather/current")
    WeatherData getCurrentWeather();

    @GetMapping("/api/v1/weather/forecast")
    WeatherDataForecast getWeatherForecast(@RequestParam("days") int days);
}