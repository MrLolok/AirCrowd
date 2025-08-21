package it.lorenzoangelino.aircrowd.weather.exceptions;

public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class APIException extends WeatherServiceException {
        public APIException(String message) {
            super("API Error: " + message);
        }

        public APIException(String message, Throwable cause) {
            super("API Error: " + message, cause);
        }
    }

    public static class ConfigurationException extends WeatherServiceException {
        public ConfigurationException(String message) {
            super("Configuration Error: " + message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super("Configuration Error: " + message, cause);
        }
    }

    public static class DataProcessingException extends WeatherServiceException {
        public DataProcessingException(String message) {
            super("Data Processing Error: " + message);
        }

        public DataProcessingException(String message, Throwable cause) {
            super("Data Processing Error: " + message, cause);
        }
    }
}
