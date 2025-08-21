package it.lorenzoangelino.aircrowd.weather.publisher;

import it.lorenzoangelino.aircrowd.common.kafka.producer.KafkaProducerService;
import it.lorenzoangelino.aircrowd.common.models.locations.GeographicalLocation;
import it.lorenzoangelino.aircrowd.weather.services.WeatherService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherPublisherImpl implements WeatherPublisher {
    private final WeatherService weatherService;
    private final KafkaProducerService kafkaProducerService;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> publishingTask;

    public WeatherPublisherImpl(WeatherService weatherService, KafkaProducerService kafkaProducerService) {
        this.weatherService = weatherService;
        this.kafkaProducerService = kafkaProducerService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "weather-publisher");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public void start() {
        LOGGER.info("Starting Weather Publisher");
        publishingTask = scheduler.scheduleAtFixedRate(
                this::publishWeatherData, 0, Duration.ofMinutes(15).toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    @PreDestroy
    public void shutdown() {
        LOGGER.info("Shutting down Weather Publisher");
        if (publishingTask != null && !publishingTask.isCancelled()) {
            publishingTask.cancel(true);
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void publishWeatherData() {
        try {
            GeographicalLocation location = new GeographicalLocation("Naples Airport", 40.88, 14.28);
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            weatherService
                    .getWeatherDataProvider()
                    .getWeatherDataForecast(location, today, tomorrow)
                    .thenAccept(forecast -> {
                        forecast.getData().forEach(data -> {
                            try {
                                kafkaProducerService.send("weather-data", data);
                                LOGGER.debug("Published weather data: {}", data.getId());
                            } catch (Exception e) {
                                LOGGER.error("Failed to publish weather data: {}", data.getId(), e);
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        LOGGER.error("Failed to fetch weather data for publishing", throwable);
                        return null;
                    });

        } catch (Exception e) {
            LOGGER.error("Error in weather data publishing cycle", e);
        }
    }
}
