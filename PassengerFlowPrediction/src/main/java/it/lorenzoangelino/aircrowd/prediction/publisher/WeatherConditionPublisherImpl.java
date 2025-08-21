package it.lorenzoangelino.aircrowd.prediction.publisher;

import it.lorenzoangelino.aircrowd.common.kafka.consumer.AbstractKafkaStreamsProvider;
import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProviderImpl;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeatherConditionPublisherImpl extends AbstractKafkaStreamsProvider implements WeatherConditionPublisher {
    private final String topic;
    private final KafkaProducerService kafkaProducerService;
    private final Logger logger;

    public WeatherConditionPublisherImpl(
            String inputTopic, String outputTopic, KafkaProducerService kafkaProducerService) {
        super(inputTopic);
        this.topic = outputTopic;
        this.kafkaProducerService = kafkaProducerService;
        this.logger = LogManager.getLogger(WeatherConditionProviderImpl.class);
    }

    @Override
    protected void setup(KStream<String, String> stream) {
        stream.mapValues(value -> Mapper.fromJson(value, WeatherData.class))
                .mapValues(WeatherCondition::new)
                .foreach(this::publish);
    }

    @Override
    public void publish(String key, WeatherCondition condition) {
        this.logger.info("Saving weather condition of {}...", key);
        String json = Mapper.toJson(condition);
        kafkaProducerService.send(this.topic, key, json);
        this.logger.info("Weather condition saved");
    }
}
