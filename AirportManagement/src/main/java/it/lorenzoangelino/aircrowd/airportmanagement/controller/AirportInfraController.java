package it.lorenzoangelino.aircrowd.airportmanagement.controller;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.APIResponse;
import it.lorenzoangelino.aircrowd.airportmanagement.dto.AirportInfraData;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Gate;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.SecurityCheckpoint;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.Terminal;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.services.AirportInfraService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/infra")
@RequiredArgsConstructor
@Slf4j
public class AirportInfraController {

    private final AirportInfraService airportInfraService;

    @GetMapping("/overview")
    public ResponseEntity<@NotNull APIResponse<AirportInfraData>> getInfrastructureOverview() {
        AirportInfraData data = airportInfraService.getInfrastructureOverview();
        return ResponseEntity.ok(APIResponse.success(data, "Infrastructure overview retrieved successfully"));
    }

    // Terminal Management
    @GetMapping("/terminals")
    public ResponseEntity<@NotNull APIResponse<Page<Terminal>>> getAllTerminals(Pageable pageable) {
        Page<Terminal> terminals = airportInfraService.getAllTerminals(pageable);
        return ResponseEntity.ok(APIResponse.success(terminals, "Terminals retrieved successfully"));
    }

    @GetMapping("/terminals/{id}")
    public ResponseEntity<@NotNull APIResponse<Terminal>> getTerminalById(@PathVariable Long id) {
        return airportInfraService
                .getTerminalById(id)
                .map(terminal -> ResponseEntity.ok(APIResponse.success(terminal, "Terminal found")))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/terminals")
    public ResponseEntity<@NotNull APIResponse<Terminal>> createTerminal(@Valid @RequestBody Terminal terminal) {
        Terminal created = airportInfraService.createTerminal(terminal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(created, "Terminal created successfully"));
    }

    @PutMapping("/terminals/{id}")
    public ResponseEntity<@NotNull APIResponse<Terminal>> updateTerminal(
            @PathVariable Long id, @Valid @RequestBody Terminal terminal) {
        Terminal updated = airportInfraService.updateTerminal(id, terminal);
        return ResponseEntity.ok(APIResponse.success(updated, "Terminal updated successfully"));
    }

    @DeleteMapping("/terminals/{id}")
    public ResponseEntity<@NotNull APIResponse<Void>> deleteTerminal(@PathVariable Long id) {
        airportInfraService.deleteTerminal(id);
        return ResponseEntity.ok(APIResponse.success(null, "Terminal deleted successfully"));
    }

    // Gate Management
    @GetMapping("/gates")
    public ResponseEntity<@NotNull APIResponse<Page<Gate>>> getAllGates(Pageable pageable) {
        Page<Gate> gates = airportInfraService.getAllGates(pageable);
        return ResponseEntity.ok(APIResponse.success(gates, "Gates retrieved successfully"));
    }

    @GetMapping("/gates/{id}")
    public ResponseEntity<@NotNull APIResponse<Gate>> getGateById(@PathVariable Long id) {
        return airportInfraService
                .getGateById(id)
                .map(gate -> ResponseEntity.ok(APIResponse.success(gate, "Gate found")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gates/terminal/{terminalId}")
    public ResponseEntity<@NotNull APIResponse<List<Gate>>> getGatesByTerminal(@PathVariable Long terminalId) {
        List<Gate> gates = airportInfraService.getGatesByTerminal(terminalId);
        return ResponseEntity.ok(
                APIResponse.success(gates, String.format("Found %d gates for terminal", gates.size())));
    }

    @GetMapping("/gates/status/{status}")
    public ResponseEntity<@NotNull APIResponse<List<Gate>>> getGatesByStatus(@PathVariable GateStatus status) {
        List<Gate> gates = airportInfraService.getGatesByStatus(status);
        return ResponseEntity.ok(
                APIResponse.success(gates, String.format("Found %d gates with status %s", gates.size(), status)));
    }

    @PostMapping("/gates")
    public ResponseEntity<@NotNull APIResponse<Gate>> createGate(@Valid @RequestBody Gate gate) {
        Gate created = airportInfraService.createGate(gate);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(created, "Gate created successfully"));
    }

    @PutMapping("/gates/{id}")
    public ResponseEntity<@NotNull APIResponse<Gate>> updateGate(@PathVariable Long id, @Valid @RequestBody Gate gate) {
        Gate updated = airportInfraService.updateGate(id, gate);
        return ResponseEntity.ok(APIResponse.success(updated, "Gate updated successfully"));
    }

    @PatchMapping("/gates/{id}/status")
    public ResponseEntity<@NotNull APIResponse<Gate>> updateGateStatus(@PathVariable Long id, @RequestParam GateStatus status) {
        Gate updated = airportInfraService.updateGateStatus(id, status);
        return ResponseEntity.ok(APIResponse.success(updated, "Gate status updated successfully"));
    }

    @DeleteMapping("/gates/{id}")
    public ResponseEntity<@NotNull APIResponse<Void>> deleteGate(@PathVariable Long id) {
        airportInfraService.deleteGate(id);
        return ResponseEntity.ok(APIResponse.success(null, "Gate deleted successfully"));
    }

    // Security Checkpoint Management
    @GetMapping("/security-checkpoints")
    public ResponseEntity<@NotNull APIResponse<Page<SecurityCheckpoint>>> getAllSecurityCheckpoints(Pageable pageable) {
        Page<SecurityCheckpoint> checkpoints = airportInfraService.getAllSecurityCheckpoints(pageable);
        return ResponseEntity.ok(APIResponse.success(checkpoints, "Security checkpoints retrieved successfully"));
    }

    @GetMapping("/security-checkpoints/{id}")
    public ResponseEntity<@NotNull APIResponse<SecurityCheckpoint>> getSecurityCheckpointById(@PathVariable Long id) {
        return airportInfraService
                .getSecurityCheckpointById(id)
                .map(checkpoint -> ResponseEntity.ok(APIResponse.success(checkpoint, "Security checkpoint found")))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/security-checkpoints/terminal/{terminalId}")
    public ResponseEntity<@NotNull APIResponse<List<SecurityCheckpoint>>> getSecurityCheckpointsByTerminal(
            @PathVariable Long terminalId) {
        List<SecurityCheckpoint> checkpoints = airportInfraService.getSecurityCheckpointsByTerminal(terminalId);
        return ResponseEntity.ok(APIResponse.success(
                checkpoints, String.format("Found %d security checkpoints for terminal", checkpoints.size())));
    }

    @GetMapping("/security-checkpoints/status/{status}")
    public ResponseEntity<@NotNull APIResponse<List<SecurityCheckpoint>>> getSecurityCheckpointsByStatus(
            @PathVariable CheckpointStatus status) {
        List<SecurityCheckpoint> checkpoints = airportInfraService.getSecurityCheckpointsByStatus(status);
        return ResponseEntity.ok(APIResponse.success(
                checkpoints,
                String.format("Found %d security checkpoints with status %s", checkpoints.size(), status)));
    }

    @PostMapping("/security-checkpoints")
    public ResponseEntity<@NotNull APIResponse<SecurityCheckpoint>> createSecurityCheckpoint(
            @Valid @RequestBody SecurityCheckpoint checkpoint) {
        SecurityCheckpoint created = airportInfraService.createSecurityCheckpoint(checkpoint);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(created, "Security checkpoint created successfully"));
    }

    @PutMapping("/security-checkpoints/{id}")
    public ResponseEntity<@NotNull APIResponse<SecurityCheckpoint>> updateSecurityCheckpoint(
            @PathVariable Long id, @Valid @RequestBody SecurityCheckpoint checkpoint) {
        SecurityCheckpoint updated = airportInfraService.updateSecurityCheckpoint(id, checkpoint);
        return ResponseEntity.ok(APIResponse.success(updated, "Security checkpoint updated successfully"));
    }

    @DeleteMapping("/security-checkpoints/{id}")
    public ResponseEntity<@NotNull APIResponse<Void>> deleteSecurityCheckpoint(@PathVariable Long id) {
        airportInfraService.deleteSecurityCheckpoint(id);
        return ResponseEntity.ok(APIResponse.success(null, "Security checkpoint deleted successfully"));
    }
}
