package it.lorenzoangelino.aircrowd.prediction.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "weather-criticality")
public record WeatherCriticalityConfig(
        List<ConfigCriticalitySettings.Parameter> parameters,
        double minCriticalityValue,
        double maxCriticalityValue) {
}