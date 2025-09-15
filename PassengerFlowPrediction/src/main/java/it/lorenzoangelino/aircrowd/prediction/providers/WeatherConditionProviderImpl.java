package it.lorenzoangelino.aircrowd.prediction.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
import it.lorenzoangelino.aircrowd.prediction.factories.ConditionFactory;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.individual.impl.WeatherCondition;
import it.lorenzoangelino.aircrowd.prediction.services.WeatherDataStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherConditionProviderImpl implements WeatherConditionProvider {
    private final Map<String, WeatherCondition> cache = new HashMap<>();
    private final ObjectMapper objectMapper;
    private final ConditionFactory conditionFactory;
    private final WeatherDataStorageService weatherDataStorageService;

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

    @KafkaListener(topics = "${condition-providers.weather.kafka-topic}")
    public void handleWeatherData(String key, String value) {
        try {
            WeatherData weatherData = objectMapper.readValue(value, WeatherData.class);
            
            // Store weather data to Iceberg table
            weatherDataStorageService.storeWeatherData(weatherData);
            
            WeatherCondition condition = conditionFactory.createWeatherCondition(weatherData);
            
            if (condition != null && 
                (!cache.containsKey(key) || 
                 cache.get(key).getData().creation().isBefore(condition.getData().creation()))) {
                
                log.info("New weather condition calculated (Key: {}, Value: {})", key, condition.toString());
                cache.put(key, condition);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse weather data JSON: {}", value, e);
        }
    }
}
