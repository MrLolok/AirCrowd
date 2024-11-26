package it.lorenzoangelino.aircrowd.prediction.providers;

import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import it.lorenzoangelino.aircrowd.common.kafka.consumer.AbstractKafkaStreamsProvider;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;

public class WeatherConditionProviderImpl extends AbstractKafkaStreamsProvider implements WeatherConditionProvider {
    private final Map<String, WeatherCondition> cache;
    private final Logger logger;

    public WeatherConditionProviderImpl(String inputKafkaTopic) {
        super(inputKafkaTopic);
        this.cache = new HashMap<>();
        this.logger = LogManager.getLogger(WeatherConditionProviderImpl.class);
    }

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        List<WeatherCondition> conditions = cache.values()
            .stream()
            .filter(condition -> {
                LocalDateTime datetime = condition.getData().datetime();
                return datetime.isEqual(start) || datetime.isEqual(end) || (datetime.isAfter(start) && datetime.isBefore(end));
            })
            .toList();
        if (conditions.isEmpty())
            return Optional.empty();
        Condition condition = new SimpleCombinedCondition(conditions);
        return Optional.of(condition);
    }

    @Override
    public Map<String, WeatherCondition> getCache() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    protected void setup(KStream<String, String> stream) {
        stream
                .mapValues(value -> Mapper.fromJson(value, WeatherCondition.class))
                .filter((key, value) -> !cache.containsKey(key) || cache.get(key).getData().creation().isBefore(value.getData().creation()))
                .foreach((key, value) -> {
                    this.logger.info("New weather condition calculated (Key: {}, Value: {})", key, value.toString());
                    cache.put(key, value);
                });
    }
}
