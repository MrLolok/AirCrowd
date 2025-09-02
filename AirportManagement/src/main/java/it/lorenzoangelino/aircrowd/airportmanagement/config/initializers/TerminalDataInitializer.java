package it.lorenzoangelino.aircrowd.airportmanagement.config.initializers;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.TerminalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class TerminalDataInitializer implements CommandLineRunner {

    private final TerminalRepository terminalRepository;

    @Override
    public void run(String... args) {
        initializeTerminals();
    }

    private void initializeTerminals() {
        if (terminalRepository.count() > 0) {
            log.info("Terminals already exist, skipping initialization");
            return;
        }

        create("T1", "Terminal 1 - International", 15000);
        create("T2", "Terminal 2 - Domestic", 12000);

        log.info("Initialized default terminals");
    }

    private void create(String code, String name, Integer capacity) {
        Terminal terminal = new Terminal();
        terminal.setTerminalCode(code);
        terminal.setName(name);
        terminal.setCapacity(capacity);

        terminalRepository.save(terminal);
        log.info("Created terminal: {} - {}", code, name);
    }
}
