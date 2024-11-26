package it.lorenzoangelino.aircrowd.common.kafka.producer;

import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
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
        Properties props = getProperties();
        this.producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public Future<RecordMetadata> send(String topic, String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        this.logger.info("Key {} - Value {}", key, value);
        return this.producer.send(record);
    }

    @Override
    public Future<RecordMetadata> send(String topic, IdentifiableModel<?> entity) {
        this.logger.info("Sending message to topic: {}", topic);
        return this.send(topic, entity.getId().toString(), Mapper.toJson(entity));
    }

    @Override
    public void close() {
        this.logger.info("Closing Kafka producer...");
        if (this.producer != null)
            this.producer.close();
        this.logger.info("Kafka producer closed.");
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SETTINGS.bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KAFKA_SETTINGS.producer().keySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KAFKA_SETTINGS.producer().valueSerializer());
        return props;
    }
}
