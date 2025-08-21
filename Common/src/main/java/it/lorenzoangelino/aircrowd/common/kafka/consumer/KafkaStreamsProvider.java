package it.lorenzoangelino.aircrowd.common.kafka.consumer;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.configs.defaults.ConfigKafkaConsumerSettings;

public interface KafkaStreamsProvider {
    ConfigKafkaConsumerSettings KAFKA_SETTINGS =
            ConfigProvider.getInstance().loadConfig("kafka-consumer", ConfigKafkaConsumerSettings.class);

    void start();

    void close();
}
