package it.lorenzoangelino.aircrowd.weather.api.callbacks;

import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import it.lorenzoangelino.aircrowd.weather.mapper.Mapper;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ContentType;

@FunctionalInterface
public interface WeatherForecastCallback extends ResponseCallback {
    void accept(WeatherForecastResponse response);

    @Override
    default void onSuccess(SimpleHttpResponse response) {
        if (response.getBody().getContentType() != ContentType.APPLICATION_JSON)
            throw new UnsupportedOperationException("Content type of the weather forecast not supported.");
        WeatherForecastResponse weatherForecastResponse = Mapper.fromJson(response.getBodyText(), WeatherForecastResponse.class);
        accept(weatherForecastResponse);
    }
}