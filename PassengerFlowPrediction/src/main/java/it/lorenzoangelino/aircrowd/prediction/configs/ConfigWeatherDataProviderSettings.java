package it.lorenzoangelino.aircrowd.prediction.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigWeatherDataProviderSettings(
        @JsonProperty("weather-data-topic") String weatherDataTopic,
        @JsonProperty("weather-condition-topic") String weatherConditionTopic) {
}
