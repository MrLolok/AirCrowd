package it.lorenzoangelino.aircrowd.prediction.configs;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ConfigCriticalitySettings(
        List<Parameter> parameters,
        @JsonProperty("min-criticality-value") double minCriticalityValue,
        @JsonProperty("max-criticality-value") double maxCriticalityValue
) {
    public record Parameter(
            String name,
            double deviation,
            double weight,
            @JsonProperty("min-value") double minValue,
            @JsonProperty("max-value") double maxValue,
            @JsonProperty("inverted-impact") boolean invertedImpact
    ) {}

    public @Nullable Parameter getParameter(String name) {
        for (Parameter parameter : parameters)
            if (parameter.name().equals(name))
                return parameter;
        return null;
    }
}