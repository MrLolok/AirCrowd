package it.lorenzoangelino.aircrowd.prediction.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.kafka.consumer.AbstractKafkaStreamsProvider;
import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProviderImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;

@Slf4j
public class WeatherConditionPublisherImpl extends AbstractKafkaStreamsProvider implements WeatherConditionPublisher {
    private final String topic;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    public WeatherConditionPublisherImpl(
            String inputTopic, String outputTopic, KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        super(inputTopic);
        this.topic = outputTopic;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void setup(KStream<String, String> stream) {
        stream.mapValues(value -> {
                    try {
                        return objectMapper.readValue(value, WeatherData.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse weather data JSON: {}", value, e);
                        return null;
                    }
                })
                .filter((key, value) -> value != null)
                .mapValues(WeatherCondition::new)
                .foreach(this::publish);
    }

    @Override
    public void publish(String key, WeatherCondition condition) {
        log.info("Saving weather condition of {}...", key);
        try {
            String json = objectMapper.writeValueAsString(condition);
            kafkaProducerService.send(this.topic, key, json);
            log.info("Weather condition saved");
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather condition: {}", condition, e);
        }
    }
}
