package it.lorenzoangelino.aircrowd.airportmanagement.dto;

import it.lorenzoangelino.aircrowd.airportmanagement.entities.Gate;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.SecurityCheckpoint;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.User;
import java.time.LocalDateTime;
import java.util.List;

public record AirportInfraData(
        LocalDateTime timestamp,
        List<TerminalSummary> terminals,
        List<GateSummary> gates,
        List<SecurityCheckpointSummary> securityCheckpoints,
        List<UserSummary> users,
        InfrastructureStats stats) {

    public record TerminalSummary(
            Long id, String terminalCode, String name, Integer capacity, int gatesCount, int securityCheckpointsCount) {
        public static TerminalSummary from(Terminal terminal) {
            return new TerminalSummary(
                    terminal.getId(),
                    terminal.getTerminalCode(),
                    terminal.getName(),
                    terminal.getCapacity(),
                    terminal.getGates() != null ? terminal.getGates().size() : 0,
                    terminal.getSecurityCheckpoints() != null
                            ? terminal.getSecurityCheckpoints().size()
                            : 0);
        }
    }

    public record GateSummary(
            Long id,
            String gateNumber,
            String name,
            String status,
            String type,
            Integer maxPassengers,
            String terminalCode) {
        public static GateSummary from(Gate gate) {
            return new GateSummary(
                    gate.getId(),
                    gate.getGateNumber(),
                    gate.getName(),
                    gate.getStatus().name(),
                    gate.getType().name(),
                    gate.getMaxPassengers(),
                    gate.getTerminal() != null ? gate.getTerminal().getTerminalCode() : null);
        }
    }

    public record SecurityCheckpointSummary(
            Long id,
            String checkpointCode,
            String name,
            String status,
            Integer lanesCount,
            Integer activeLanes,
            Integer hourlyCapacity,
            String terminalCode) {
        public static SecurityCheckpointSummary from(SecurityCheckpoint checkpoint) {
            return new SecurityCheckpointSummary(
                    checkpoint.getId(),
                    checkpoint.getCheckpointCode(),
                    checkpoint.getName(),
                    checkpoint.getStatus().name(),
                    checkpoint.getLanesCount(),
                    checkpoint.getActiveLanes(),
                    checkpoint.getHourlyCapacity(),
                    checkpoint.getTerminal() != null ? checkpoint.getTerminal().getTerminalCode() : null);
        }
    }

    public record UserSummary(Long id, String email) {
        public static UserSummary from(User user) {
            return new UserSummary(user.getId(), user.getEmail());
        }
    }

    public record InfrastructureStats(
            int totalTerminals,
            int totalGates,
            int availableGates,
            int occupiedGates,
            int maintenanceGates,
            int totalSecurityCheckpoints,
            int operationalCheckpoints,
            int totalSecurityLanes,
            int activeSecurityLanes,
            int totalUsers) {}
}
