package it.lorenzoangelino.aircrowd.common.models.flights;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import it.lorenzoangelino.aircrowd.common.models.flights.enums.FlightType;

import java.time.LocalDateTime;

public record FlightData(String code, FlightType type, LocalDateTime datetime, short seats) implements IdentifiableModel<String> {
    @Override
    public String getId() {
        return code;
    }
}
