package it.lorenzoangelino.aircrowd.weather.provider;

import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.spark.SparkConfig;
import it.lorenzoangelino.aircrowd.weather.api.callbacks.WeatherForecastCallback;
import it.lorenzoangelino.aircrowd.weather.api.clients.APIClientRequester;
import it.lorenzoangelino.aircrowd.weather.api.params.QueryParam;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import org.apache.spark.sql.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WeatherDataProviderImpl implements WeatherDataProvider {
    private final APIClientRequester requester;

    public WeatherDataProviderImpl(APIClientRequester requester) {
        this.requester = requester;
    }

    @Override
    public CompletableFuture<WeatherDataForecast> fetchWeatherDataForecast(GeographicalLocation location) {
        CompletableFuture<WeatherDataForecast> future = new CompletableFuture<>();
        WeatherForecastCallback callback = response -> {
            if (RESPONSES_SETTINGS.save())
                ResponseSaver.save(RESPONSES_SETTINGS.table(), response);

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
    public CompletableFuture<WeatherData> fetchWeatherData(GeographicalLocation location, LocalDateTime date) {
        CompletableFuture<WeatherData> future = new CompletableFuture<>();
        fetchWeatherDataForecast(location).whenComplete((forecast, throwable) -> {
            if (throwable != null)
                future.completeExceptionally(throwable);
            for (WeatherData data : forecast.hourlyWeatherData()) {
                boolean timeIsEqual = data.datetime().getYear() == date.getYear()
                        && data.datetime().getMonth() == date.getMonth()
                        && data.datetime().getDayOfMonth() == date.getDayOfMonth()
                        && data.datetime().getHour() == date.getHour();
                if (timeIsEqual) {
                    future.complete(data);
                    break;
                }
            }
        });
        return future;
    }

    private @NotNull WeatherData getWeatherData(WeatherForecastResponse response, GeographicalLocation location, int index) {
        WeatherForecastResponse.HourlyData data = response.hourly();
        return new WeatherData(location,
                LocalDateTime.now(), LocalDateTime.parse(data.time().get(index), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                data.temperature().get(index), data.relativeHumidity().get(index), data.dewPoint().get(index),
                data.precipitationProbability().get(index), data.rain().get(index), data.showers().get(index),
                data.snowfall().get(index), data.pressure().get(index), data.cloudCover().get(index),
                data.visibility().get(index), data.windSpeed().get(index), data.windDirection().get(index));
    }

    private static class ResponseSaver {
        private final static SparkSession SPARK_SESSION = SparkConfig.getSparkSession();
        private final static String AUTHOR_NAME = "WeatherService";

        public static void save(String table, WeatherForecastResponse... responses) {
            Dataset<Row> record = getDataset(responses);
            record.write()
                .format("iceberg")
                .mode(SaveMode.Append)
                .save(table);
        }

        private static Dataset<Row> getDataset(WeatherForecastResponse... responses) {
            List<ResponseRecord> records = getRecords(responses);
            return SPARK_SESSION.createDataFrame(records, ResponseRecord.class);
        }

        private static List<ResponseRecord> getRecords(WeatherForecastResponse... responses) {
            String datetime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            return Arrays.stream(responses)
                .map(response -> new ResponseRecord(datetime, AUTHOR_NAME, Mapper.toJson(response)))
                .toList();
        }

        private record ResponseRecord(String datetime, String author, String body) {}
    }
}
