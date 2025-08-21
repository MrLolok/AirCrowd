package it.lorenzoangelino.aircrowd.common.kafka.producer;

import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Future<RecordMetadata> send(String topic, String key, String value) {
        log.info("Sending message to topic: {}, key: {}", topic, key);

        // Wrap in CompletableFuture for simpler handling
        return CompletableFuture.supplyAsync(() -> {
            try {
                var sendResult = kafkaTemplate.send(topic, key, value).get();
                log.info("Message sent successfully to topic: {}", topic);
                return sendResult.getRecordMetadata();
            } catch (Exception e) {
                log.error("Failed to send message to topic: {}", topic, e);
                throw new RuntimeException("Failed to send message", e);
            }
        });
    }

    @Override
    public Future<RecordMetadata> send(String topic, IdentifiableModel<?> entity) {
        return send(topic, entity.getId().toString(), Mapper.toJson(entity));
    }

    @Override
    public void close() {
        log.info("KafkaProducerService closed - managed by Spring");
    }
}
