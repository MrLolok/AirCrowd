package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.kafka.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.common.models.weather.WeatherDataForecast;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherPublisherImpl implements WeatherPublisher {

    private final WeatherService weatherService;
    private final KafkaProducerService kafkaProducerService;

    @Value("${weather.publisher.weather-data-output-topic}")
    private String weatherDataOutputTopic;
    
    @Value("${weather.publisher.task.delay}")
    private long taskDelay;
    
    @Value("${weather.publisher.task.period}")
    private long taskPeriod;
    
    @Value("${weather.publisher.task.unit}")
    private TimeUnit taskUnit;
    
    @Value("${weather.location.name}")
    private String locationName;
    
    @Value("${weather.location.latitude}")
    private double locationLatitude;
    
    @Value("${weather.location.longitude}")
    private double locationLongitude;
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
                locationName,
                locationLatitude,
                locationLongitude);

        // Start automatic publishing
        start(defaultLocation);
    }

    @Override
    public void start(GeographicalLocation location) {
        if (!running) {
            log.info("Starting Weather Publisher for location: {}", location.name());
            this.defaultLocation = location;
            this.running = true;

            publishingTask =
                    scheduler.scheduleAtFixedRate(this::publishWeatherData, taskDelay, taskPeriod, taskUnit);

            log.info("Weather Publisher scheduled to run every {} {}", taskPeriod, taskUnit);
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
        String topic = weatherDataOutputTopic;

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
