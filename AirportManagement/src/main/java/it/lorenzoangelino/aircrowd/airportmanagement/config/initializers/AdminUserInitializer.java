package it.lorenzoangelino.aircrowd.airportmanagement.config.initializers;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.User;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initialize();
    }

    private void initialize() {
        String adminEmail = "admin@email.it";
        String adminPassword = "admin";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping initialization");
            return;
        }

        User admin = new User();
        admin.setEmail("admin@aircrowd.local");
        admin.setPassword(passwordEncoder.encode(adminPassword));

        userRepository.save(admin);
        log.info("Created default admin user: {} {}", adminEmail, adminPassword);
    }
}
