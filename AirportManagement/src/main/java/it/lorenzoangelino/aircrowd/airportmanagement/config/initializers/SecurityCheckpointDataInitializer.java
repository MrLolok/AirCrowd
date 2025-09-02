package it.lorenzoangelino.aircrowd.airportmanagement.config.initializers;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.SecurityCheckpoint;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.SecurityCheckpointRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.TerminalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class SecurityCheckpointDataInitializer implements CommandLineRunner {

    private final SecurityCheckpointRepository checkpointRepository;
    private final TerminalRepository terminalRepository;

    @Override
    public void run(String... args) {
        initialize();
    }

    private void initialize() {
        if (checkpointRepository.count() > 0) {
            log.info("Security checkpoints already exist, skipping initialization");
            return;
        }

        Terminal t1 = terminalRepository.findByTerminalCode("T1").orElse(null);
        Terminal t2 = terminalRepository.findByTerminalCode("T2").orElse(null);

        if (t1 == null || t2 == null) {
            log.warn("Terminals not found, skipping security checkpoint initialization");
            return;
        }

        // Terminal 1 - International checkpoints
        create("SEC-T1-01", "Security Checkpoint T1 - Central", 8, 6, 400, t1);
        create("SEC-T1-02", "Security Checkpoint T1 - Fast Track", 4, 3, 200, t1);
        create("SEC-T1-03", "Security Checkpoint T1 - Priority", 2, 2, 150, t1);

        // Terminal 2 - Domestic checkpoints
        create("SEC-T2-01", "Security Checkpoint T2 - Main", 6, 4, 350, t2);
        create("SEC-T2-02", "Security Checkpoint T2 - Fast Track", 3, 2, 180, t2);

        log.info("Initialized default security checkpoints");
    }

    private void create(
            String checkpointCode,
            String name,
            Integer lanesCount,
            Integer activeLanes,
            Integer hourlyCapacity,
            Terminal terminal) {
        SecurityCheckpoint checkpoint = new SecurityCheckpoint();
        checkpoint.setCheckpointCode(checkpointCode);
        checkpoint.setName(name);
        checkpoint.setStatus(CheckpointStatus.OPERATIONAL);
        checkpoint.setLanesCount(lanesCount);
        checkpoint.setActiveLanes(activeLanes);
        checkpoint.setHourlyCapacity(hourlyCapacity);
        checkpoint.setTerminal(terminal);

        checkpointRepository.save(checkpoint);
        log.info("Created security checkpoint: {} in terminal {}", checkpointCode, terminal.getTerminalCode());
    }
}
