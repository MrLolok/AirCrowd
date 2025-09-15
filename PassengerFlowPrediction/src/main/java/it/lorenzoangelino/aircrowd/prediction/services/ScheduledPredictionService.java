package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledPredictionService {

    private final PassengerFlowPredictionService predictionService;
    private final PredictionStorageService storageService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Value("${schedule.delay}")
    private long scheduleDelay;
    
    @Value("${schedule.period}")
    private long schedulePeriod;
    
    @Value("${schedule.unit}")
    private TimeUnit scheduleUnit;

    @PostConstruct
    public void initialize() {
        log.info("Initializing Scheduled Prediction Service");
        schedulePredictonProcess();
    }

    private void schedulePredictonProcess() {
        scheduler.scheduleAtFixedRate(
                this::generateAndStorePredictions,
                scheduleDelay,
                schedulePeriod,
                scheduleUnit
        );
        log.info("Prediction process scheduled with delay: {} {}, period: {} {}",
                scheduleDelay, scheduleUnit,
                schedulePeriod, scheduleUnit);
    }

    private void generateAndStorePredictions() {
        try {
            log.info("Starting scheduled prediction generation");
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextHour = now.plusHours(1);
            
            PassengerFlowPrediction prediction = predictionService.predictPassengerFlow(now, nextHour);
            
            storageService.storePrediction(prediction);
            
            log.info("Prediction generated and stored successfully: {}", prediction.predictionId());
        } catch (Exception e) {
            log.error("Scheduled prediction generation failed: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stopScheduler() {
        log.info("Stopping prediction scheduler");
        scheduler.shutdown();
    }
}