package it.lorenzoangelino.aircrowd.flightschedule.exceptions;

public class FlightScheduleException extends RuntimeException {

    public FlightScheduleException(String message) {
        super(message);
    }

    public FlightScheduleException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class FileProcessingException extends FlightScheduleException {
        public FileProcessingException(String message) {
            super("File Processing Error: " + message);
        }

        public FileProcessingException(String message, Throwable cause) {
            super("File Processing Error: " + message, cause);
        }
    }

    public static class DataValidationException extends FlightScheduleException {
        public DataValidationException(String message) {
            super("Data Validation Error: " + message);
        }

        public DataValidationException(String message, Throwable cause) {
            super("Data Validation Error: " + message, cause);
        }
    }

    public static class SparkProcessingException extends FlightScheduleException {
        public SparkProcessingException(String message) {
            super("Spark Processing Error: " + message);
        }

        public SparkProcessingException(String message, Throwable cause) {
            super("Spark Processing Error: " + message, cause);
        }
    }

    public static class ConfigurationException extends FlightScheduleException {
        public ConfigurationException(String message) {
            super("Configuration Error: " + message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super("Configuration Error: " + message, cause);
        }
    }
}
