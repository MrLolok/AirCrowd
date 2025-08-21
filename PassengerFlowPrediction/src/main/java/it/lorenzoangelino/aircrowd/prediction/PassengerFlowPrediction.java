package it.lorenzoangelino.aircrowd.prediction;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerServiceImpl;
import it.lorenzoangelino.aircrowd.common.spark.SparkConfig;
import it.lorenzoangelino.aircrowd.prediction.configs.ConfigWeatherDataProviderSettings;
import it.lorenzoangelino.aircrowd.prediction.providers.FlightFlowConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.FlightFlowConditionProviderImpl;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProvider;
import it.lorenzoangelino.aircrowd.prediction.providers.WeatherConditionProviderImpl;
import it.lorenzoangelino.aircrowd.prediction.publisher.WeatherConditionPublisher;
import it.lorenzoangelino.aircrowd.prediction.publisher.WeatherConditionPublisherImpl;
import org.apache.spark.sql.SparkSession;

public final class PassengerFlowPrediction {
    private final SparkSession SPARK_SESSION = SparkConfig.getSparkSession();
    private static final KafkaProducerService KAFKA_PRODUCER_SERVICE = new KafkaProducerServiceImpl();

    public static void main(String[] args) {
        ConfigWeatherDataProviderSettings weatherDataProviderSettings = ConfigProvider.getInstance()
                .loadConfig("weather-data-provider", ConfigWeatherDataProviderSettings.class);
        WeatherConditionPublisher weatherConditionPublisher = new WeatherConditionPublisherImpl(
                weatherDataProviderSettings.weatherDataTopic(),
                weatherDataProviderSettings.weatherConditionTopic(),
                KAFKA_PRODUCER_SERVICE);
        weatherConditionPublisher.start();

        // Individual condition providers
        String weatherConditionTopic = weatherDataProviderSettings.weatherConditionTopic();
        WeatherConditionProvider weatherConditionProvider = new WeatherConditionProviderImpl(weatherConditionTopic);
        SparkSession sparkSession = SparkConfig.getSparkSession();
        FlightFlowConditionProvider flightFlowConditionProvider = new FlightFlowConditionProviderImpl(sparkSession);
    }
}
