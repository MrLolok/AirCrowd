package it.lorenzoangelino.aircrowd.prediction.exceptions;

public class PredictionProcessingException extends RuntimeException {
    
    public PredictionProcessingException(String message) {
        super(message);
    }
    
    public PredictionProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}