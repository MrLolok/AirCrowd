package it.lorenzoangelino.aircrowd.common.kafka;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.RecordMetadata;

public interface KafkaProducerService {

    Future<RecordMetadata> send(String topic, String key, String value);

    Future<RecordMetadata> send(String topic, IdentifiableModel<?> entity);

    void close();
}
