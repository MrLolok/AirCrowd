package it.lorenzoangelino.aircrowd.common.configs.defaults;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigKafkaConsumerSettings(
        @JsonProperty("bootstrap-servers") String bootstrapServers,
        Consumer consumer
) {
    public record Consumer(
        @JsonProperty("enable-autocommit") boolean enableAutocommit,
        @JsonProperty("group-id") String groupId,
        @JsonProperty("auto-offset-reset") String autoOffsetReset,
        @JsonProperty("key-serializer") String keyDeserializer,
        @JsonProperty("value-serializer") String valueDeserializer
    ) {}
}
