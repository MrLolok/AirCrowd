package it.lorenzoangelino.aircrowd.airportmanagement.repositories;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TerminalRepository extends JpaRepository<@NotNull Terminal, @NotNull Long> {

    Optional<Terminal> findByTerminalCode(String terminalCode);
}
