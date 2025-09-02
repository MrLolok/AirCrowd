package it.lorenzoangelino.aircrowd.weather.exceptions;

public class WeatherDataProcessingException extends RuntimeException {

    public WeatherDataProcessingException(String message) {
        super("Data Processing Error: " + message);
    }

    public WeatherDataProcessingException(String message, Throwable cause) {
        super("Data Processing Error: " + message, cause);
    }
}
