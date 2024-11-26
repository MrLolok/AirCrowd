package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.common.kafka.consumer.KafkaStreamsProvider;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;

import java.util.Map;

public interface WeatherConditionProvider extends ConditionProvider, KafkaStreamsProvider {
    Map<String, WeatherCondition> getCache();
}
