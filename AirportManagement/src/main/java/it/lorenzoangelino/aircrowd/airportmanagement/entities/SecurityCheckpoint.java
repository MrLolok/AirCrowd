package it.lorenzoangelino.aircrowd.airportmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.CheckpointStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "security_checkpoints")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SecurityCheckpoint extends BaseEntity {

    @Column(name = "checkpoint_code", unique = true, nullable = false)
    @NotBlank(message = "Checkpoint code is required.")
    @Size(max = 20, message = "Checkpoint code is too long.")
    @Pattern(
            regexp = "^SEC-T[0-9A-Z]+-[0-9]+$",
            message = "Checkpoint code format: SEC-T{terminal}-{number} (e.g., SEC-T1-01).")
    private String checkpointCode;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Checkpoint name is required.")
    @Size(max = 100, message = "Checkpoint name is too long.")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required.")
    private CheckpointStatus status = CheckpointStatus.OPERATIONAL;

    @Column(name = "lanes_count", nullable = false)
    @Min(value = 1, message = "Must have at least 1 lane.")
    @Max(value = 20, message = "Too many lanes.")
    private Integer lanesCount;

    @Column(name = "active_lanes", nullable = false)
    @Min(value = 0, message = "Active lanes cannot be negative.")
    private Integer activeLanes = 0;

    @Column(name = "hourly_capacity")
    @Min(value = 50, message = "Minimum hourly capacity is 50.")
    @Max(value = 2000, message = "Maximum hourly capacity is 2000.")
    private Integer hourlyCapacity;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Terminal is required.")
    @ToString.Exclude
    private Terminal terminal;
}
