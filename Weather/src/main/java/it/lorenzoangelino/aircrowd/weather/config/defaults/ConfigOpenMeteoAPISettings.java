package it.lorenzoangelino.aircrowd.weather.config.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigOpenMeteoAPISettings(@JsonProperty("base_url") String baseURL,
                                         @JsonProperty("forecast_days") int forecastDays,
                                         String hourly,
                                         String timezone) {



}
