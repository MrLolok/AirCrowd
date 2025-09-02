package it.lorenzoangelino.aircrowd.prediction.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.kafka.consumer.AbstractKafkaStreamsProvider;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;

@Slf4j
public class WeatherConditionProviderImpl extends AbstractKafkaStreamsProvider implements WeatherConditionProvider {
    private final Map<String, WeatherCondition> cache;
    private final ObjectMapper objectMapper;

    public WeatherConditionProviderImpl(String inputKafkaTopic, ObjectMapper objectMapper) {
        super(inputKafkaTopic);
        this.cache = new HashMap<>();
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        List<WeatherCondition> conditions = cache.values().stream()
                .filter(condition -> {
                    LocalDateTime datetime = condition.getData().datetime();
                    return datetime.isEqual(start)
                            || datetime.isEqual(end)
                            || (datetime.isAfter(start) && datetime.isBefore(end));
                })
                .toList();
        if (conditions.isEmpty()) return Optional.empty();
        Condition condition = new SimpleCombinedCondition(conditions);
        return Optional.of(condition);
    }

    @Override
    public Map<String, WeatherCondition> getCache() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    protected void setup(KStream<String, String> stream) {
        stream.mapValues(value -> {
                    try {
                        return objectMapper.readValue(value, WeatherCondition.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to parse weather condition JSON: {}", value, e);
                        return null;
                    }
                })
                .filter((key, value) -> value != null)
                .filter((key, value) -> !cache.containsKey(key)
                        || cache.get(key)
                                .getData()
                                .creation()
                                .isBefore(value.getData().creation()))
                .foreach((key, value) -> {
                    log.info("New weather condition calculated (Key: {}, Value: {})", key, value.toString());
                    cache.put(key, value);
                });
    }
}
