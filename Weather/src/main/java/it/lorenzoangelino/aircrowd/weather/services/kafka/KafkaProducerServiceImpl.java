package it.lorenzoangelino.aircrowd.weather.services.kafka;

import it.lorenzoangelino.aircrowd.weather.entities.IdentifiableEntity;
import it.lorenzoangelino.aircrowd.weather.mapper.Mapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerServiceImpl implements KafkaProducerService {
    private final Logger logger;
    private final KafkaProducer<String, String> producer;

    public KafkaProducerServiceImpl() {
        this.logger = LogManager.getLogger(KafkaProducerServiceImpl.class);
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_SETTINGS.bootstrapServers());
        props.put("key.serializer", KAFKA_SETTINGS.producer().keySerializer());
        props.put("value.serializer", KAFKA_SETTINGS.producer().valueSerializer());
        this.producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public Future<RecordMetadata> send(String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_SETTINGS.topic(), key, value);
        this.logger.info("Sending message to topic: {}", record.topic());
        this.logger.info("Key {} - Value {}", key, value);
        return this.producer.send(record);
    }

    @Override
    public Future<RecordMetadata> send(IdentifiableEntity<?> entity) {
        return this.send(entity.getId().toString(), Mapper.toJson(entity));
    }

    @Override
    public void close() {
        this.logger.info("Closing Kafka producer...");
        if (this.producer != null)
            this.producer.close();
        this.logger.info("Kafka producer closed.");
    }
}
