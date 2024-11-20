package it.lorenzoangelino.aircrowd.weather.services.kafka;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import it.lorenzoangelino.aircrowd.weather.configs.ConfigKafkaSettings;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public interface KafkaProducerService {
    ConfigKafkaSettings KAFKA_SETTINGS = ConfigProvider.getInstance().loadConfig("kafka", ConfigKafkaSettings.class);

    Future<RecordMetadata> send(String key, String value);

    Future<RecordMetadata> send(IdentifiableModel<?> entity);

    void close();
}
