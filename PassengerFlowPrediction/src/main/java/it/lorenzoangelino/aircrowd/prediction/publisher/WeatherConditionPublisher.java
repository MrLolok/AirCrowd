package it.lorenzoangelino.aircrowd.prediction.publisher;

import it.lorenzoangelino.aircrowd.common.kafka.consumer.KafkaStreamsProvider;
import it.lorenzoangelino.aircrowd.prediction.model.conditions.individual.impl.WeatherCondition;

public interface WeatherConditionPublisher extends KafkaStreamsProvider {
    void publish(String key, WeatherCondition condition);
}
