package it.lorenzoangelino.aircrowd.common.configs.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigKafkaProducerSettings(
        @JsonProperty("bootstrap-servers") String bootstrapServers,
        Producer producer
) {
    public record Producer(
        @JsonProperty("key-serializer") String keySerializer,
        @JsonProperty("value-serializer") String valueSerializer
    ) {}
}
