package it.lorenzoangelino.aircrowd.weather;

import it.lorenzoangelino.aircrowd.common.configs.ConfigProvider;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.publisher.WeatherPublisher;
import it.lorenzoangelino.aircrowd.weather.publisher.WeatherPublisherImpl;
import it.lorenzoangelino.aircrowd.weather.services.kafka.KafkaProducerService;
import it.lorenzoangelino.aircrowd.weather.services.kafka.KafkaProducerServiceImpl;
import it.lorenzoangelino.aircrowd.weather.services.weather.WeatherService;
import it.lorenzoangelino.aircrowd.weather.services.weather.WeatherServiceImpl;

public final class Weather {
    private static final KafkaProducerService KAFKA_PRODUCER_SERVICE = new KafkaProducerServiceImpl();
    private static final WeatherService WEATHER_SERVICE = new WeatherServiceImpl();

    public static void main(String[] args) {
        GeographicalLocation analyzedWeatherLocation = ConfigProvider.getInstance().loadConfig("location", GeographicalLocation.class);
        WeatherPublisher publisher = new WeatherPublisherImpl(WEATHER_SERVICE, KAFKA_PRODUCER_SERVICE);
        publisher.startScheduledTask(analyzedWeatherLocation);
    }
}
