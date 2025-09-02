package it.lorenzoangelino.aircrowd.airportmanagement.exception;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.APIResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NotNull APIResponse<Void>> handleGenericException(Exception exception) {
        log.error("An unhandled exception was caught by APIExceptionHandler:", exception);
        HttpStatus status = resolveResponseStatus(exception.getClass());
        String exceptionType = convertToSnakeCase(exception.getClass().getSimpleName());
        return ResponseEntity.status(status).body(APIResponse.error(exception.getMessage(), exceptionType));
    }

    private HttpStatus resolveResponseStatus(Class<?> clazz) {
        ResponseStatus annotation = clazz.getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String convertToSnakeCase(String pascalCase) {
        if (pascalCase == null || pascalCase.isEmpty()) return "UNKNOWN_EXCEPTION";
        String result = pascalCase.replaceAll("(?<=\\p{Lower})(?=\\p{Upper})", "_");
        return result.toUpperCase();
    }
}
