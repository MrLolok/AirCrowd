package it.lorenzoangelino.aircrowd.common.models.flights;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public record FlightFlowData(@NotNull List<FlightData> flights) implements IdentifiableModel<String> {
    @Override
    public String getId() {
        if (flights.isEmpty())
            return "Empty-" + hashCode();

        List<FlightData> sorted = getSortedFlights();
        String firstFlightDataTime = sorted.getFirst().datetime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String lastFlightDataTime = sorted.getLast().datetime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return String.format("%s-%s", firstFlightDataTime, lastFlightDataTime);
    }

    public List<FlightData> getSortedFlights() {
        return flights.stream()
            .sorted(Comparator.comparing(FlightData::datetime).thenComparing(FlightData::type))
            .toList();
    }
}
