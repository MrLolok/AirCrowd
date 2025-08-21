package it.lorenzoangelino.aircrowd.weather.configs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.concurrent.TimeUnit;

public record ConfigPublisherSettings(
        @JsonProperty("weather-data-output-topic") String weatherDataOutputTopic, Task task) {
    public record Task(long delay, long period, TimeUnit unit) {}
}
