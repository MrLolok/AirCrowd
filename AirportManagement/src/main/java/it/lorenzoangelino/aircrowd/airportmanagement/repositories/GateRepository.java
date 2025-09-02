package it.lorenzoangelino.aircrowd.airportmanagement.repositories;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.Gate;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GateRepository extends JpaRepository<@NotNull Gate, @NotNull Long> {

    List<Gate> findByTerminal(Terminal terminal);

    Optional<Gate> findByGateNumberAndTerminal(String gateNumber, Terminal terminal);

    List<Gate> findByStatus(GateStatus status);
}
