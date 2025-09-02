package it.lorenzoangelino.aircrowd.prediction.exceptions;

public class PredictionConfigurationException extends RuntimeException {
    
    public PredictionConfigurationException(String message) {
        super(message);
    }
    
    public PredictionConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}