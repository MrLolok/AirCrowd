package it.lorenzoangelino.aircrowd.airportmanagement.repositories;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.SecurityCheckpoint;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityCheckpointRepository extends JpaRepository<@NotNull SecurityCheckpoint, @NotNull Long> {

    List<SecurityCheckpoint> findByTerminal(Terminal terminal);

    List<SecurityCheckpoint> findByStatus(CheckpointStatus status);
}
