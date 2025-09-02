package it.lorenzoangelino.aircrowd.airportmanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "terminals")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Terminal extends BaseEntity {

    @Column(name = "terminal_code", unique = true, nullable = false)
    @NotBlank(message = "Terminal code is required.")
    @Size(max = 10, message = "Terminal code is too long.")
    @Pattern(regexp = "^T[0-9A-Z]+$", message = "Terminal code must start with 'T' followed by numbers or letters.")
    private String terminalCode;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Terminal name is required.")
    @Size(max = 100, message = "Terminal name is too long.")
    private String name;

    @Column(name = "capacity")
    @Min(value = 0, message = "Capacity must be positive.")
    private Integer capacity;

    @JsonManagedReference
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "terminal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Gate> gates = new HashSet<>();

    @JsonManagedReference
    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "terminal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SecurityCheckpoint> securityCheckpoints = new HashSet<>();
}
