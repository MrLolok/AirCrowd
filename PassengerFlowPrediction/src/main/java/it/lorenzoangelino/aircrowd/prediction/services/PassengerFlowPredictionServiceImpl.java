package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.spark.SparkConfig;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigWeatherDataProviderSettings;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.Condition;
import it.lorenzoangelino.aircrowd.prediction.models.conditions.combined.impl.SimpleCombinedCondition;
import it.lorenzoangelino.aircrowd.prediction.providers.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.apache.spark.sql.SparkSession;

@Getter
public class PassengerFlowPredictionServiceImpl implements PassengerFlowPredictionService {
    private final WeatherConditionProvider weatherConditionProvider;
    private final FlightFlowConditionProvider flightFlowConditionProvider;

    public PassengerFlowPredictionServiceImpl() {
        ConfigWeatherDataProviderSettings weatherDataProviderSettings = ConfigProvider.getInstance()
                .loadConfig("weather-data-provider", ConfigWeatherDataProviderSettings.class);
        String weatherConditionTopic = weatherDataProviderSettings.weatherConditionTopic();
        this.weatherConditionProvider = new WeatherConditionProviderImpl(weatherConditionTopic);
        SparkSession sparkSession = SparkConfig.getSparkSession();
        this.flightFlowConditionProvider = new FlightFlowConditionProviderImpl(sparkSession);
    }

    @Override
    public Optional<Condition> getCondition(LocalDateTime start, LocalDateTime end) {
        Optional<Condition> weatherCondition = weatherConditionProvider.getCondition(start, end);
        Optional<Condition> flightFlowCondition = flightFlowConditionProvider.getCondition(start, end);
        List<Condition> conditions = List.of(weatherCondition.orElseThrow(), flightFlowCondition.orElseThrow());
        Condition condition = new SimpleCombinedCondition(conditions);
        return Optional.of(condition);
    }
}
