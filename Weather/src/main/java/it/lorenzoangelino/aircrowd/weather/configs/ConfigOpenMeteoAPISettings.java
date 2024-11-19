package it.lorenzoangelino.aircrowd.weather.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigOpenMeteoAPISettings(@JsonProperty("base_url") String baseURL,
                                         @JsonProperty("forecast_days") int forecastDays,
                                         String hourly,
                                         String timezone) {



}
