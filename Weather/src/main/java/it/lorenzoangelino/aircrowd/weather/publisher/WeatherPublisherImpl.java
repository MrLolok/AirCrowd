package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.config.WeatherConfigurationProperties;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherPublisherImpl implements WeatherPublisher {

    private final WeatherService weatherService;
    private final KafkaProducerService kafkaProducerService;
    private final WeatherConfigurationProperties weatherConfig;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "weather-publisher");
        t.setDaemon(true);
        return t;
    });

    private ScheduledFuture<?> publishingTask;
    private volatile boolean running = false;
    private GeographicalLocation defaultLocation;

    @PostConstruct
    public void init() {
        // Initialize default location from configuration
        this.defaultLocation = new GeographicalLocation(
                weatherConfig.location().name(),
                weatherConfig.location().latitude(),
                weatherConfig.location().longitude());

        // Start automatic publishing
        start(defaultLocation);
    }

    @Override
    public void start(GeographicalLocation location) {
        if (!running) {
            log.info("Starting Weather Publisher for location: {}", location.name());
            this.defaultLocation = location;
            this.running = true;

            var task = weatherConfig.publisher().task();
            publishingTask =
                    scheduler.scheduleAtFixedRate(this::publishWeatherData, task.delay(), task.period(), task.unit());

            log.info("Weather Publisher scheduled to run every {} {}", task.period(), task.unit());
        } else {
            log.info("Weather Publisher is already running");
        }
    }

    @Override
    public void stop() {
        if (running && publishingTask != null) {
            log.info("Stopping Weather Publisher");
            publishingTask.cancel(false);
            running = false;
        }
    }

    @Override
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Weather Publisher");
        stop();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void publishWeatherDataForecast(WeatherDataForecast data) {
        String topic = weatherConfig.publisher().weatherDataOutputTopic();

        try {
            data.hourlyWeatherData().forEach(weatherData -> {
                try {
                    kafkaProducerService.send(topic, weatherData);
                    log.debug("Published weather data to topic '{}': {}", topic, weatherData.getId());
                } catch (Exception e) {
                    log.error("Failed to publish weather data to topic '{}': {}", topic, weatherData.getId(), e);
                }
            });

            log.info(
                    "Successfully published {} weather data points to topic '{}'",
                    data.hourlyWeatherData().size(),
                    topic);

        } catch (Exception e) {
            log.error("Error publishing weather data forecast to topic '{}'", topic, e);
        }
    }

    private void publishWeatherData() {
        if (defaultLocation == null) {
            log.warn("No location set for weather publishing");
            return;
        }

        try {
            log.debug("Publishing weather data for location: {}", defaultLocation.name());

            weatherService
                    .getWeatherForecast(defaultLocation)
                    .thenAccept(this::publishWeatherDataForecast)
                    .exceptionally(throwable -> {
                        log.error("Failed to fetch weather data for publishing", throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error in weather data publishing cycle", e);
        }
    }
}
