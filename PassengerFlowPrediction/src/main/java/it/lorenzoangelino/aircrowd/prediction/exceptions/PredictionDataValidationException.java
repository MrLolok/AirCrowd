package it.lorenzoangelino.aircrowd.prediction.exceptions;

public class PredictionDataValidationException extends RuntimeException {
    
    public PredictionDataValidationException(String message) {
        super(message);
    }
    
    public PredictionDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}