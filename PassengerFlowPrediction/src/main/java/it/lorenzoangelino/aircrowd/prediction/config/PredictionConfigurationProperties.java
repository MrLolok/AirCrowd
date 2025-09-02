package it.lorenzoangelino.aircrowd.prediction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "prediction")
public record PredictionConfigurationProperties(
        WeatherDataProvider weatherDataProvider,
        WeatherCriticality weatherCriticality,
        FlightFlowCriticality flightFlowCriticality,
        KafkaConsumer kafkaConsumer) {

    public record WeatherDataProvider(
            String weatherDataTopic,
            String weatherConditionTopic) {}

    public record WeatherCriticality(
            List<CriticalityParameter> parameters,
            Double minCriticalityValue,
            Double maxCriticalityValue) {}

    public record FlightFlowCriticality(
            List<CriticalityParameter> parameters,
            Double minCriticalityValue,
            Double maxCriticalityValue) {}

    public record CriticalityParameter(
            String name,
            Double weight,
            Double minValue,
            Double maxValue,
            Double deviation,
            Boolean invertedImpact) {}

    public record KafkaConsumer(
            String bootstrapServers,
            Consumer consumer) {}

    public record Consumer(
            Boolean enableAutocommit,
            String groupId,
            String autoOffsetReset,
            String keyDeserializer,
            String valueDeserializer) {}
}