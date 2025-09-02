package it.lorenzoangelino.aircrowd.airportmanagement.repositories;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.User;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<@NotNull User, @NotNull Long> {
    boolean existsByEmail(@NotNull String email);
}
