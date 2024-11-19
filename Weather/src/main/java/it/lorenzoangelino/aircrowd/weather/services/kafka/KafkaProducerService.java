package it.lorenzoangelino.aircrowd.weather.services.kafka;

import it.lorenzoangelino.aircrowd.weather.config.ConfigProvider;
import it.lorenzoangelino.aircrowd.weather.config.defaults.ConfigKafkaSettings;
import it.lorenzoangelino.aircrowd.weather.entities.IdentifiableEntity;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public interface KafkaProducerService {
    ConfigKafkaSettings KAFKA_SETTINGS = ConfigProvider.getInstance().loadConfig("kafka", ConfigKafkaSettings.class);

    Future<RecordMetadata> send(String key, String value);

    Future<RecordMetadata> send(IdentifiableEntity<?> entity);

    void close();
}
