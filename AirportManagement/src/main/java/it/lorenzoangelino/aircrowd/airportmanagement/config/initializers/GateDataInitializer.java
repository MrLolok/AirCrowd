package it.lorenzoangelino.aircrowd.airportmanagement.config.initializers;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.Gate;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateType;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.GateRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.TerminalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class GateDataInitializer implements CommandLineRunner {

    private final GateRepository gateRepository;
    private final TerminalRepository terminalRepository;

    @Override
    public void run(String... args) {
        initialize();
    }

    private void initialize() {
        if (gateRepository.count() > 0) {
            log.info("Gates already exist, skipping initialization");
            return;
        }

        Terminal t1 = terminalRepository.findByTerminalCode("T1").orElse(null);
        Terminal t2 = terminalRepository.findByTerminalCode("T2").orElse(null);

        if (t1 == null || t2 == null) {
            log.warn("Terminals not found, skipping gate initialization");
            return;
        }

        // Terminal 1 - International gates
        create("A1", "Gate A1 - Wide Body", GateType.INTERNATIONAL, 400, t1);
        create("A2", "Gate A2 - Wide Body", GateType.INTERNATIONAL, 400, t1);
        create("A3", "Gate A3 - Standard", GateType.INTERNATIONAL, 300, t1);
        create("A4", "Gate A4 - Standard", GateType.INTERNATIONAL, 300, t1);
        create("B1", "Gate B1 - Mixed Use", GateType.MIXED, 350, t1);
        create("B2", "Gate B2 - Mixed Use", GateType.MIXED, 350, t1);

        // Terminal 2 - Domestic gates
        create("C1", "Gate C1 - Domestic", GateType.DOMESTIC, 250, t2);
        create("C2", "Gate C2 - Domestic", GateType.DOMESTIC, 250, t2);
        create("C3", "Gate C3 - Domestic", GateType.DOMESTIC, 200, t2);
        create("D1", "Gate D1 - Charter", GateType.CHARTER, 300, t2);

        log.info("Initialized default gates");
    }

    private void create(String gateNumber, String name, GateType type, Integer maxPassengers, Terminal terminal) {
        Gate gate = new Gate();
        gate.setGateNumber(gateNumber);
        gate.setName(name);
        gate.setType(type);
        gate.setStatus(GateStatus.AVAILABLE);
        gate.setMaxPassengers(maxPassengers);
        gate.setTerminal(terminal);

        gateRepository.save(gate);
        log.info("Created gate: {} in terminal {}", gateNumber, terminal.getTerminalCode());
    }
}
