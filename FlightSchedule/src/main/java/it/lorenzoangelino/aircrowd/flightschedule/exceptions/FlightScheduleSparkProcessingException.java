package it.lorenzoangelino.aircrowd.flightschedule.exceptions;

public class FlightScheduleSparkProcessingException extends RuntimeException {

    public FlightScheduleSparkProcessingException(String message) {
        super("Spark Processing Error: " + message);
    }

    public FlightScheduleSparkProcessingException(String message, Throwable cause) {
        super("Spark Processing Error: " + message, cause);
    }
}