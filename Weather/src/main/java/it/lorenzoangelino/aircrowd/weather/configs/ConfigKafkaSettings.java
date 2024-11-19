package it.lorenzoangelino.aircrowd.weather.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigKafkaSettings(
        String topic,
        @JsonProperty("bootstrap-servers") String bootstrapServers,
        Producer producer
) {
    public record Producer(
       @JsonProperty("key-serializer") String keySerializer,
       @JsonProperty("value-serializer") String valueSerializer
    ) {}
}
