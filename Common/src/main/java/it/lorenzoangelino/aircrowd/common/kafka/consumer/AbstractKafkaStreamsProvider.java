package it.lorenzoangelino.aircrowd.common.kafka.consumer;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

public abstract class AbstractKafkaStreamsProvider implements KafkaStreamsProvider {
    private final Logger logger;
    private final Properties properties;

    @Getter
    private @Nullable KafkaStreams streams;

    public AbstractKafkaStreamsProvider(String topic) {
        this.logger = LogManager.getLogger(AbstractKafkaStreamsProvider.class);
        this.properties = getProperties();
        initializeKafkaStreams(topic);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void start() {
        this.logger.info("Starting Kafka streams...");
        if (this.streams != null)
            this.streams.start();
        this.logger.info("Kafka streams has been started.");
    }

    @Override
    public void close() {
        this.logger.info("Closing Kafka streams...");
        if (this.streams != null)
            this.streams.close();
        this.logger.info("Kafka streams has been closed.");
    }

    protected abstract void setup(KStream<String, String> stream);

    private void initializeKafkaStreams(String inputTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(inputTopic);
        setup(stream);
        this.streams = new KafkaStreams(builder.build(), properties);
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SETTINGS.bootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, KAFKA_SETTINGS.consumer().enableAutocommit());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_SETTINGS.consumer().groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KAFKA_SETTINGS.consumer().autoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KAFKA_SETTINGS.consumer().keyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KAFKA_SETTINGS.consumer().valueDeserializer());
        return props;
    }
}
