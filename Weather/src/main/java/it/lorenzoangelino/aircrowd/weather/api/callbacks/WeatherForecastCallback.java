package it.lorenzoangelino.aircrowd.weather.api.callbacks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.weather.api.responses.WeatherForecastResponse;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ContentType;

public interface WeatherForecastCallback extends ResponseCallback {
    void accept(WeatherForecastResponse response);
    
    ObjectMapper getObjectMapper();

    @Override
    default void onSuccess(SimpleHttpResponse response) {
        if (!response.getBody().getContentType().isSameMimeType(ContentType.APPLICATION_JSON))
            throw new UnsupportedOperationException("Content type of the weather forecast not supported.");
        try {
            WeatherForecastResponse weatherForecastResponse =
                    getObjectMapper().readValue(response.getBodyText(), WeatherForecastResponse.class);
            accept(weatherForecastResponse);
        } catch (JsonProcessingException e) {
            onFailure(e);
        }
    }
}
