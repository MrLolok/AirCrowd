package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.weather.entities.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.entities.WeatherLocation;
import it.lorenzoangelino.aircrowd.weather.services.kafka.KafkaProducerService;
import it.lorenzoangelino.aircrowd.weather.services.weather.WeatherService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WeatherPublisherImpl implements WeatherPublisher {
    private final Logger logger;
    private final ScheduledExecutorService scheduler;
    private final WeatherService weatherService;
    private final KafkaProducerService kafkaProducerService;

    public WeatherPublisherImpl(WeatherService weatherService, KafkaProducerService kafkaProducerService) {
        this.logger = LogManager.getLogger(WeatherPublisherImpl.class);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.weatherService = weatherService;
        this.kafkaProducerService = kafkaProducerService;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopScheduledTask));
    }

    @Override
    public void startScheduledTask(WeatherLocation location) {
        this.logger.info("Starting automatic weather publisher...");
        Runnable task = () -> weatherService.getWeatherForecast(location).whenComplete((forecast, throwable) -> {
            if (throwable != null)
                this.logger.error(throwable);
            if (forecast != null) {
                this.logger.info("Publishing weather forecast of size {}", forecast.hourlyWeatherData().size());
                publishWeatherDataForecast(forecast);
            }
        });
        scheduler.scheduleAtFixedRate(task, PUBLISHER_SETTINGS.delay(), PUBLISHER_SETTINGS.period(), PUBLISHER_SETTINGS.unit());
        this.logger.info("Automatic weather publisher started.");
    }

    @Override
    public void stopScheduledTask() {
        this.logger.info("Stopping automatic weather publisher...");
        if (this.scheduler != null)
            this.scheduler.shutdownNow();
        this.logger.info("Automatic weather publisher stopped.");
    }

    @Override
    public void publishWeatherDataForecast(WeatherDataForecast data) {
        data.hourlyWeatherData().forEach(kafkaProducerService::send);
    }
}
