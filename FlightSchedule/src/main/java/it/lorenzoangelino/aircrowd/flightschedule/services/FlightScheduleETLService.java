package it.lorenzoangelino.aircrowd.flightschedule.services;

import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleFileProcessingException;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleETLProcess;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightScheduleETLService {

    private final FlightScheduleETLProcess etlProcess;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Value("${flight-schedule.etl.source}")
    private String etlSource;
    
    @Value("${flight-schedule.etl.destination}")
    private String etlDestination;
    
    @Value("${flight-schedule.schedule.delay}")
    private long scheduleDelay;
    
    @Value("${flight-schedule.schedule.period}")
    private long schedulePeriod;
    
    @Value("${flight-schedule.schedule.unit}")
    private TimeUnit scheduleUnit;

    @PostConstruct
    public void initialize() {
        log.info("Initializing FlightSchedule ETL Service with source: {} and destination: {}", etlSource, etlDestination);
        scheduleETLProcess();
    }

    private void scheduleETLProcess() {
        scheduler.scheduleAtFixedRate(
                this::runETL,
                scheduleDelay,
                schedulePeriod,
                scheduleUnit
        );
        log.info("ETL process scheduled with delay: {} {}, period: {} {}",
                scheduleDelay, scheduleUnit,
                schedulePeriod, scheduleUnit);
    }

    private void runETL() {
        try {
            log.info("Starting scheduled ETL process");
            etlProcess.process(etlSource, etlDestination);
            log.info("ETL process completed successfully");
        } catch (Exception e) {
            log.error("ETL process failed: {}", e.getMessage(), e);
            throw new FlightScheduleFileProcessingException("Scheduled ETL process failed", e);
        }
    }

    @PreDestroy
    public void stopScheduler() {
        log.info("Stopping ETL scheduler");
        scheduler.shutdown();
    }
}