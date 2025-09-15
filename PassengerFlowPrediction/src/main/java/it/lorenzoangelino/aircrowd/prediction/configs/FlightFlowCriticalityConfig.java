package it.lorenzoangelino.aircrowd.prediction.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "flight-flow-criticality")
public record FlightFlowCriticalityConfig(
        List<ConfigCriticalitySettings.Parameter> parameters,
        double minCriticalityValue,
        double maxCriticalityValue) {
}