package it.lorenzoangelino.aircrowd.flightschedule.exceptions;

public class FlightScheduleDataValidationException extends RuntimeException {

    public FlightScheduleDataValidationException(String message) {
        super("Data Validation Error: " + message);
    }

    public FlightScheduleDataValidationException(String message, Throwable cause) {
        super("Data Validation Error: " + message, cause);
    }
}