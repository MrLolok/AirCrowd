package it.lorenzoangelino.aircrowd.airportmanagement.services;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.AirportInfraData;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Gate;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.SecurityCheckpoint;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.User;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.GateRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.SecurityCheckpointRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.TerminalRepository;
import it.lorenzoangelino.aircrowd.airportmanagement.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AirportInfraService {

    private final TerminalRepository terminalRepository;
    private final GateRepository gateRepository;
    private final SecurityCheckpointRepository securityCheckpointRepository;
    private final UserRepository userRepository;

    @Cacheable("infra-overview")
    public AirportInfraData getInfrastructureOverview() {
        log.info("Fetching infrastructure overview");

        try {
            List<Terminal> terminals = terminalRepository.findAll();
            List<Gate> gates = gateRepository.findAll();
            List<SecurityCheckpoint> checkpoints = securityCheckpointRepository.findAll();
            List<User> users = userRepository.findAll();

            List<AirportInfraData.TerminalSummary> terminalSummaries = terminals.stream()
                    .map(AirportInfraData.TerminalSummary::from)
                    .toList();

            List<AirportInfraData.GateSummary> gateSummaries =
                    gates.stream().map(AirportInfraData.GateSummary::from).toList();

            List<AirportInfraData.SecurityCheckpointSummary> checkpointSummaries = checkpoints.stream()
                    .map(AirportInfraData.SecurityCheckpointSummary::from)
                    .toList();

            List<AirportInfraData.UserSummary> userSummaries =
                    users.stream().map(AirportInfraData.UserSummary::from).toList();

            AirportInfraData.InfrastructureStats stats = calculateInfrastructureStats(gates, checkpoints, users);

            return new AirportInfraData(
                    LocalDateTime.now(), terminalSummaries, gateSummaries, checkpointSummaries, userSummaries, stats);

        } catch (Exception e) {
            log.error("Error fetching infrastructure overview", e);
            throw new RuntimeException("Failed to fetch infrastructure overview", e);
        }
    }

    // Terminal Management
    public Page<Terminal> getAllTerminals(Pageable pageable) {
        return terminalRepository.findAll(pageable);
    }

    public Optional<Terminal> getTerminalById(Long id) {
        return terminalRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public Terminal createTerminal(Terminal terminal) {
        log.info("Creating terminal: {}", terminal.getTerminalCode());
        return terminalRepository.save(terminal);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public Terminal updateTerminal(Long id, Terminal terminal) {
        Terminal existing = terminalRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Terminal not found with ID: " + id));

        terminal.setId(id);
        terminal.setCreatedAt(existing.getCreatedAt());
        terminal.setVersion(existing.getVersion());

        log.info("Updating terminal: {} (ID: {})", terminal.getTerminalCode(), id);
        return terminalRepository.save(terminal);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public void deleteTerminal(Long id) {
        if (!terminalRepository.existsById(id)) {
            throw new EntityNotFoundException("Terminal not found with ID: " + id);
        }
        log.info("Deleting terminal with ID: {}", id);
        terminalRepository.deleteById(id);
    }

    // Gate Management
    public Page<Gate> getAllGates(Pageable pageable) {
        return gateRepository.findAll(pageable);
    }

    public List<Gate> getGatesByTerminal(Long terminalId) {
        Terminal terminal = terminalRepository
                .findById(terminalId)
                .orElseThrow(() -> new EntityNotFoundException("Terminal not found with ID: " + terminalId));
        return gateRepository.findByTerminal(terminal);
    }

    public List<Gate> getGatesByStatus(GateStatus status) {
        return gateRepository.findByStatus(status);
    }

    public Optional<Gate> getGateById(Long id) {
        return gateRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public Gate createGate(Gate gate) {
        validateGate(gate);
        log.info(
                "Creating gate: {} in terminal {}",
                gate.getGateNumber(),
                gate.getTerminal().getTerminalCode());
        return gateRepository.save(gate);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public Gate updateGate(Long id, Gate gate) {
        Gate existing = gateRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gate not found with ID: " + id));

        gate.setId(id);
        gate.setCreatedAt(existing.getCreatedAt());
        gate.setVersion(existing.getVersion());

        log.info("Updating gate: {} (ID: {})", gate.getGateNumber(), id);
        return gateRepository.save(gate);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public Gate updateGateStatus(Long id, GateStatus status) {
        Gate gate = gateRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gate not found with ID: " + id));

        gate.setStatus(status);
        log.info("Updated status for gate {} to: {}", gate.getGateNumber(), status);
        return gateRepository.save(gate);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public void deleteGate(Long id) {
        if (!gateRepository.existsById(id)) {
            throw new EntityNotFoundException("Gate not found with ID: " + id);
        }
        log.info("Deleting gate with ID: {}", id);
        gateRepository.deleteById(id);
    }

    // Security Checkpoint Management
    public Page<SecurityCheckpoint> getAllSecurityCheckpoints(Pageable pageable) {
        return securityCheckpointRepository.findAll(pageable);
    }

    public List<SecurityCheckpoint> getSecurityCheckpointsByTerminal(Long terminalId) {
        Terminal terminal = terminalRepository
                .findById(terminalId)
                .orElseThrow(() -> new EntityNotFoundException("Terminal not found with ID: " + terminalId));
        return securityCheckpointRepository.findByTerminal(terminal);
    }

    public List<SecurityCheckpoint> getSecurityCheckpointsByStatus(CheckpointStatus status) {
        return securityCheckpointRepository.findByStatus(status);
    }

    public Optional<SecurityCheckpoint> getSecurityCheckpointById(Long id) {
        return securityCheckpointRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public SecurityCheckpoint createSecurityCheckpoint(SecurityCheckpoint checkpoint) {
        validateSecurityCheckpoint(checkpoint);
        log.info(
                "Creating security checkpoint: {} in terminal {}",
                checkpoint.getCheckpointCode(),
                checkpoint.getTerminal().getTerminalCode());
        return securityCheckpointRepository.save(checkpoint);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public SecurityCheckpoint updateSecurityCheckpoint(Long id, SecurityCheckpoint checkpoint) {
        SecurityCheckpoint existing = securityCheckpointRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Security checkpoint not found with ID: " + id));

        checkpoint.setId(id);
        checkpoint.setCreatedAt(existing.getCreatedAt());
        checkpoint.setVersion(existing.getVersion());

        log.info("Updating security checkpoint: {} (ID: {})", checkpoint.getCheckpointCode(), id);
        return securityCheckpointRepository.save(checkpoint);
    }

    @Transactional
    @CacheEvict(value = "infra-overview", allEntries = true)
    public void deleteSecurityCheckpoint(Long id) {
        if (!securityCheckpointRepository.existsById(id)) {
            throw new EntityNotFoundException("Security checkpoint not found with ID: " + id);
        }
        log.info("Deleting security checkpoint with ID: {}", id);
        securityCheckpointRepository.deleteById(id);
    }

    private void validateGate(Gate gate) {
        if (gate.getTerminal() == null
                || !terminalRepository.existsById(gate.getTerminal().getId())) {
            throw new IllegalArgumentException("Invalid terminal specified");
        }

        Optional<Gate> existing = gateRepository.findByGateNumberAndTerminal(gate.getGateNumber(), gate.getTerminal());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Gate number already exists in this terminal: " + gate.getGateNumber());
        }
    }

    private void validateSecurityCheckpoint(SecurityCheckpoint checkpoint) {
        if (checkpoint.getTerminal() == null
                || !terminalRepository.existsById(checkpoint.getTerminal().getId())) {
            throw new IllegalArgumentException("Invalid terminal specified");
        }

        if (checkpoint.getActiveLanes() != null && checkpoint.getActiveLanes() > checkpoint.getLanesCount()) {
            throw new IllegalArgumentException("Active lanes cannot exceed total lanes count");
        }
    }

    private AirportInfraData.InfrastructureStats calculateInfrastructureStats(
            List<Gate> gates, List<SecurityCheckpoint> checkpoints, List<User> users) {

        int totalGates = gates.size();
        int availableGates = (int) gates.stream()
                .filter(g -> g.getStatus() == GateStatus.AVAILABLE)
                .count();
        int occupiedGates = (int)
                gates.stream().filter(g -> g.getStatus() == GateStatus.OCCUPIED).count();
        int maintenanceGates = (int) gates.stream()
                .filter(g -> g.getStatus() == GateStatus.MAINTENANCE)
                .count();

        int totalCheckpoints = checkpoints.size();
        int operationalCheckpoints = (int) checkpoints.stream()
                .filter(c -> c.getStatus() == CheckpointStatus.OPERATIONAL)
                .count();

        int totalLanes =
                checkpoints.stream().mapToInt(SecurityCheckpoint::getLanesCount).sum();
        int activeLanes = checkpoints.stream()
                .filter(c -> c.getStatus() == CheckpointStatus.OPERATIONAL)
                .mapToInt(c -> c.getActiveLanes() != null ? c.getActiveLanes() : 0)
                .sum();

        int totalUsers = users.size();

        return new AirportInfraData.InfrastructureStats(
                (int) terminalRepository.count(),
                totalGates,
                availableGates,
                occupiedGates,
                maintenanceGates,
                totalCheckpoints,
                operationalCheckpoints,
                totalLanes,
                activeLanes,
                totalUsers);
    }
}
