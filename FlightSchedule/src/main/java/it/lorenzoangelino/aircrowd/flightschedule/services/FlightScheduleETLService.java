package it.lorenzoangelino.aircrowd.flightschedule.services;

import it.lorenzoangelino.aircrowd.flightschedule.config.FlightScheduleConfigurationProperties;
import it.lorenzoangelino.aircrowd.flightschedule.exceptions.FlightScheduleFileProcessingException;
import it.lorenzoangelino.aircrowd.flightschedule.spark.FlightScheduleETLProcess;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlightScheduleETLService {

    private final FlightScheduleConfigurationProperties config;
    private final FlightScheduleETLProcess etlProcess;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void initialize() {
        log.info("Initializing FlightSchedule ETL Service with config: {}", config);
        scheduleETLProcess();
    }

    @Bean
    public ApplicationRunner runETLOnStartup() {
        return args -> {
            log.info("Running initial ETL process on application startup");
            runETL();
        };
    }

    private void scheduleETLProcess() {
        scheduler.scheduleAtFixedRate(
                this::runETL,
                config.schedule().delay(),
                config.schedule().period(),
                config.schedule().unit()
        );
        log.info("ETL process scheduled with delay: {} {}, period: {} {}",
                config.schedule().delay(), config.schedule().unit(),
                config.schedule().period(), config.schedule().unit());
    }

    private void runETL() {
        try {
            log.info("Starting scheduled ETL process");
            etlProcess.run(config.etl().source(), config.etl().destination());
            log.info("ETL process completed successfully");
        } catch (Exception e) {
            log.error("ETL process failed: {}", e.getMessage(), e);
            throw new FlightScheduleFileProcessingException("Scheduled ETL process failed", e);
        }
    }

    public void runManualETL() {
        log.info("Running manual ETL process");
        runETL();
    }

    public void runManualETL(String source, String destination) {
        try {
            log.info("Running manual ETL process: {} -> {}", source, destination);
            etlProcess.run(source, destination);
            log.info("Manual ETL process completed successfully");
        } catch (Exception e) {
            log.error("Manual ETL process failed: {}", e.getMessage(), e);
            throw new FlightScheduleFileProcessingException("Manual ETL process failed", e);
        }
    }

    public void stopScheduler() {
        log.info("Stopping ETL scheduler");
        scheduler.shutdown();
    }
}