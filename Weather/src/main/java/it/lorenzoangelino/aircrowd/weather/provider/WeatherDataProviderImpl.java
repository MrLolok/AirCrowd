package it.lorenzoangelino.aircrowd.weather.provider;

import it.lorenzoangelino.aircrowd.weather.api.callbacks.WeatherForecastCallback;
import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherData;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherDataProviderImpl implements WeatherDataProvider {
    private final APIClientRequester requester;

    public WeatherDataProviderImpl(APIClientRequester requester) {
        this.requester = requester;
    }

    @Override
    public CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(WeatherLocation location) {
        CompletableFuture<WeatherDataForecast> future = new CompletableFuture<>();
        WeatherForecastCallback callback = response -> {
            List<WeatherData> list = new ArrayList<>();
            while (true) {
                try {
                    int index = list.size() + 1;
                    WeatherData weatherData = getWeatherData(response, location, index);
                    list.add(weatherData);
                } catch (IndexOutOfBoundsException ignored) {
                    break;
                }
            }
            WeatherDataForecast data = new WeatherDataForecast(list);
            future.complete(data);
        };
        requester.get(callback,
                QueryParam.of("latitude", String.valueOf(location.latitude())),
                QueryParam.of("longitude", String.valueOf(location.longitude()))
        );
        return future;
    }

    @Override
    public CompletableFuture<WeatherData> fetchWeatherData(WeatherLocation location, LocalDateTime date) {
        CompletableFuture<WeatherData> future = new CompletableFuture<>();
        fetchWeatherDataForecast(location).whenComplete((forecast, throwable) -> {
            if (throwable != null)
                future.completeExceptionally(throwable);
            for (WeatherData data : forecast.hourlyWeatherData()) {
                boolean timeIsEqual = data.time().getYear() == date.getYear()
                        && data.time().getMonth() == date.getMonth()
                        && data.time().getDayOfMonth() == date.getDayOfMonth()
                        && data.time().getHour() == date.getHour();
                if (timeIsEqual) {
                    future.complete(data);
                    break;
                }
            }
        });
        return future;
    }

    private @NotNull WeatherData getWeatherData(WeatherForecastResponse response, WeatherLocation location, int index) {
        WeatherForecastResponse.HourlyData data = response.hourly();
        return new WeatherData(location, LocalDateTime.parse(data.time().get(index), DateTimeFormatter.ISO_DATE_TIME),
                data.temperature().get(index), data.relativeHumidity().get(index), data.dewPoint().get(index),
                data.precipitationProbability().get(index), data.rain().get(index), data.showers().get(index),
                data.snowfall().get(index), data.pressure().get(index), data.cloudCover().get(index),
                data.visibility().get(index), data.windSpeed().get(index), data.windDirection().get(index));
    }
}
