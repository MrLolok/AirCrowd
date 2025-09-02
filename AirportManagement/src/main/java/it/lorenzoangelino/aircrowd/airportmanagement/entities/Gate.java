package it.lorenzoangelino.aircrowd.airportmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateStatus;
import it.lorenzoangelino.aircrowd.airportmanagement.entities.enums.GateType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(
        name = "gates",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"gate_number", "terminal_id"})})
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Gate extends BaseEntity {

    @Column(name = "gate_number", nullable = false)
    @NotBlank(message = "Gate number is required.")
    @Size(max = 10, message = "Gate number is too long.")
    @Pattern(
            regexp = "^[A-Z][0-9]+[A-Z]?$",
            message = "Gate number format: letter followed by numbers (e.g., A12, B23A).")
    private String gateNumber;

    @Column(name = "name")
    @Size(max = 100, message = "Gate name is too long.")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required.")
    private GateStatus status = GateStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "gate_type", nullable = false)
    @NotNull(message = "Gate type is required.")
    private GateType type;

    @Column(name = "max_passengers")
    @Min(value = 50, message = "Minimum passenger capacity is 50.")
    @Max(value = 1000, message = "Maximum passenger capacity is 1000.")
    private Integer maxPassengers;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Terminal is required.")
    @ToString.Exclude
    private Terminal terminal;
}
