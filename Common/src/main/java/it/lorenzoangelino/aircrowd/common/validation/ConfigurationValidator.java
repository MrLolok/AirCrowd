package it.lorenzoangelino.aircrowd.common.validation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration validation utility for AirCrowd services
 */
public class ConfigurationValidator {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationValidator.class);

    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static <T> ValidationResult validate(T config) {
        if (config == null) {
            return ValidationResult.failure("Configuration object cannot be null");
        }

        List<ValidationError> errors = new ArrayList<>();
        Class<?> clazz = config.getClass();

        LOGGER.info("Validating configuration for class: {}", clazz.getSimpleName());

        validateFields(config, clazz, errors, "");

        if (errors.isEmpty()) {
            LOGGER.info("Configuration validation passed for: {}", clazz.getSimpleName());
            return ValidationResult.success();
        } else {
            LOGGER.error(
                    "Configuration validation failed for: {} with {} errors", clazz.getSimpleName(), errors.size());
            return ValidationResult.failure(errors);
        }
    }

    private static void validateFields(Object config, Class<?> clazz, List<ValidationError> errors, String prefix) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldPath = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

            try {
                Object value = field.get(config);
                validateField(field, value, fieldPath, errors);
            } catch (IllegalAccessException e) {
                errors.add(new ValidationError(fieldPath, "Cannot access field", e.getMessage()));
            }
        }
    }

    private static void validateField(Field field, Object value, String fieldPath, List<ValidationError> errors) {
        // Check for @NotNull annotation
        if (field.isAnnotationPresent(NotNull.class) && value == null) {
            errors.add(new ValidationError(fieldPath, "Field cannot be null", "Required field is missing"));
            return;
        }

        if (value == null) {
            return; // Skip validation for null values unless @NotNull
        }

        // Check for @NotEmpty annotation
        if (field.isAnnotationPresent(NotEmpty.class)) {
            if (value instanceof String && ((String) value).trim().isEmpty()) {
                errors.add(
                        new ValidationError(fieldPath, "Field cannot be empty", "String value is empty or whitespace"));
            } else if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                errors.add(new ValidationError(fieldPath, "Collection cannot be empty", "Collection has no elements"));
            }
        }

        // Check for @Min annotation
        if (field.isAnnotationPresent(Min.class)) {
            Min min = field.getAnnotation(Min.class);
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                if (numValue < min.value()) {
                    errors.add(new ValidationError(
                            fieldPath, "Value must be at least " + min.value(), "Current value: " + numValue));
                }
            }
        }

        // Check for @Max annotation
        if (field.isAnnotationPresent(Max.class)) {
            Max max = field.getAnnotation(Max.class);
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                if (numValue > max.value()) {
                    errors.add(new ValidationError(
                            fieldPath, "Value must be at most " + max.value(), "Current value: " + numValue));
                }
            }
        }

        // Check for @Range annotation
        if (field.isAnnotationPresent(Range.class)) {
            Range range = field.getAnnotation(Range.class);
            if (value instanceof Number) {
                double numValue = ((Number) value).doubleValue();
                if (numValue < range.min() || numValue > range.max()) {
                    errors.add(new ValidationError(
                            fieldPath,
                            "Value must be between " + range.min() + " and " + range.max(),
                            "Current value: " + numValue));
                }
            }
        }

        // Check for @ValidUrl annotation
        if (field.isAnnotationPresent(ValidUrl.class) && value instanceof String) {
            String url = (String) value;
            if (!URL_PATTERN.matcher(url).matches()) {
                errors.add(new ValidationError(fieldPath, "Invalid URL format", "URL: " + url));
            }
        }

        // Check for @ValidEmail annotation
        if (field.isAnnotationPresent(ValidEmail.class) && value instanceof String) {
            String email = (String) value;
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                errors.add(new ValidationError(fieldPath, "Invalid email format", "Email: " + email));
            }
        }

        // Check for @ValidEnum annotation
        if (field.isAnnotationPresent(ValidEnum.class) && value instanceof String) {
            ValidEnum validEnum = field.getAnnotation(ValidEnum.class);
            String stringValue = (String) value;
            if (!Arrays.asList(validEnum.values()).contains(stringValue)) {
                errors.add(new ValidationError(
                        fieldPath,
                        "Value must be one of: " + Arrays.toString(validEnum.values()),
                        "Current value: " + stringValue));
            }
        }
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final List<ValidationError> errors;

        private ValidationResult(boolean isValid, List<ValidationError> errors) {
            this.isValid = isValid;
            this.errors = errors != null ? List.copyOf(errors) : List.of();
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, List.of(new ValidationError("", message, "")));
        }

        public static ValidationResult failure(List<ValidationError> errors) {
            return new ValidationResult(false, errors);
        }

        public boolean isValid() {
            return isValid;
        }

        public List<ValidationError> getErrors() {
            return errors;
        }

        public String getErrorSummary() {
            return errors.stream()
                    .map(error -> error.getFieldPath() + ": " + error.getMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("No errors");
        }

        public void throwIfInvalid() {
            if (!isValid) {
                throw new ConfigurationValidationException("Configuration validation failed: " + getErrorSummary());
            }
        }
    }

    public static class ValidationError {
        private final String fieldPath;
        private final String message;
        private final String details;

        public ValidationError(String fieldPath, String message, String details) {
            this.fieldPath = fieldPath;
            this.message = message;
            this.details = details;
        }

        public String getFieldPath() {
            return fieldPath;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            return String.format(
                    "ValidationError{field='%s', message='%s', details='%s'}", fieldPath, message, details);
        }
    }

    public static class ConfigurationValidationException extends RuntimeException {
        public ConfigurationValidationException(String message) {
            super(message);
        }

        public ConfigurationValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
