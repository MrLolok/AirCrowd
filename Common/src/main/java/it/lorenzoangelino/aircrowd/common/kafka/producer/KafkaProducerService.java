package it.lorenzoangelino.aircrowd.common.kafka.producer;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.configs.defaults.ConfigKafkaProducerSettings;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.RecordMetadata;

public interface KafkaProducerService {
    ConfigKafkaProducerSettings KAFKA_SETTINGS =
            ConfigProvider.getInstance().loadConfig("kafka-producer", ConfigKafkaProducerSettings.class);

    Future<RecordMetadata> send(String topic, String key, String value);

    Future<RecordMetadata> send(String topic, IdentifiableModel<?> entity);

    void close();
}
