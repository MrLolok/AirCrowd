package it.lorenzoangelino.aircrowd.weather.exceptions;

public class WeatherAPIException extends RuntimeException {

    public WeatherAPIException(String message, Throwable cause) {
        super("API Error: " + message, cause);
    }
}
