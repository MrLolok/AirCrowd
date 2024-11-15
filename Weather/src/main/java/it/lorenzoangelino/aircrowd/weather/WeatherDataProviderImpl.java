package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.weather.api.callbacks.ResponseCallback;
import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.config.defaults.ConfigOpenMeteoAPISettings;
import it.lorenzoangelino.aircrowd.weather.locations.Location;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherDataProviderImpl implements WeatherDataProvider {
    private final APIClientRequester requester;

    public WeatherDataProviderImpl(APIClientRequester requester, ConfigOpenMeteoAPISettings settings) {
        this.requester = requester;
        initAPIClientRequester(settings);
    }

    @Override
    public CompletableFuture<WeatherDataForecast> fetchWeatherData(Location location) {
        CompletableFuture<WeatherDataForecast> future = new CompletableFuture<>();
        ResponseCallback callback = response -> {
            WeatherDataForecast data = null;
            future.complete(data);
        };
        requester.get(callback,
                QueryParam.of("latitude", String.valueOf(location.latitude())),
                QueryParam.of("latitude", String.valueOf(location.longitude()))
        );
        return future;
    }

    @Override
    public CompletableFuture<WeatherData> fetchWeatherData(Location location, int hour) {
        CompletableFuture<WeatherData> future = new CompletableFuture<>();
        fetchWeatherData(location).whenComplete((forecast, throwable) -> {
            if (throwable != null)
                future.completeExceptionally(throwable);
            for (WeatherData data : forecast.hourlyWeatherData()) {
                if (data.time().getHour() == hour) {
                    future.complete(data);
                    break;
                }
            }
        });
        return future;
    }

    private void initAPIClientRequester(ConfigOpenMeteoAPISettings settings) {
        this.requester.setBaseURL(settings.baseURL());
        this.requester.setBaseQueryParams(List.of(
                QueryParam.of("hourly", settings.hourly()),
                QueryParam.of("timezone", settings.timezone()),
                QueryParam.of("forecast_days", String.valueOf(settings.forecastDays()))
        ));
    }
}
