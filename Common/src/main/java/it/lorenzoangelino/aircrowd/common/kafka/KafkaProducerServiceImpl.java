package it.lorenzoangelino.aircrowd.common.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("simpleKafkaProducerService")
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final KafkaTemplate<@NotNull String, @NotNull String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Future<RecordMetadata> send(String topic, String key, String value) {
        log.info("Sending message to topic: {}, key: {}", topic, key);

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
        try {
            String json = objectMapper.writeValueAsString(entity);
            return send(topic, entity.getId().toString(), json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize entity: {}", entity, e);
            CompletableFuture<RecordMetadata> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    @Override
    public void close() {
        log.info("KafkaProducerService closed - managed by Spring");
    }
}
