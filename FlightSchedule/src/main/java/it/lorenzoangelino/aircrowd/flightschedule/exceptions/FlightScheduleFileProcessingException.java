package it.lorenzoangelino.aircrowd.flightschedule.exceptions;

public class FlightScheduleFileProcessingException extends RuntimeException {

    public FlightScheduleFileProcessingException(String message) {
        super("File Processing Error: " + message);
    }

    public FlightScheduleFileProcessingException(String message, Throwable cause) {
        super("File Processing Error: " + message, cause);
    }
}