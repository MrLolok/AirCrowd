package it.lorenzoangelino.aircrowd.weather.services.weather;

import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.clients.HttpAPIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherData;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProvider;
import it.lorenzoangelino.aircrowd.weather.provider.WeatherDataProviderImpl;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class WeatherServiceImpl implements WeatherService {
    private final WeatherDataProvider weatherDataProvider;

    public WeatherServiceImpl() {
        APIClientRequester requester = new HttpAPIClientRequester();
        initAPIClientRequester(requester);
        requester.start();
        this.weatherDataProvider = new WeatherDataProviderImpl(requester);
    }

    @Override
    public CompletableFuture<WeatherDataForecast> getWeatherForecast(WeatherLocation location) {
        return this.weatherDataProvider.fetchWeatherDataForecast(location);
    }

    @Override
    public CompletableFuture<WeatherData> getCurrentWeather(WeatherLocation location) {
        return this.weatherDataProvider.fetchWeatherData(location, LocalDateTime.now());
    }

    @Override
    public CompletableFuture<WeatherData> getHourlyWeather(WeatherLocation location, LocalDateTime date) {
        return this.weatherDataProvider.fetchWeatherData(location, date);
    }

    private void initAPIClientRequester(APIClientRequester requester) {
        requester.setBaseURL(WeatherService.OPEN_METEO_API_SETTINGS.baseURL());
        requester.setBaseQueryParams(List.of(
                QueryParam.of("hourly", WeatherService.OPEN_METEO_API_SETTINGS.hourly()),
                QueryParam.of("timezone", WeatherService.OPEN_METEO_API_SETTINGS.timezone()),
                QueryParam.of("forecast_days", String.valueOf(WeatherService.OPEN_METEO_API_SETTINGS.forecastDays()))
        ));
    }
}
