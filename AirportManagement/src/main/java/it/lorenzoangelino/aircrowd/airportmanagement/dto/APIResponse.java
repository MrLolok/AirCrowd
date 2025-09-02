package it.lorenzoangelino.aircrowd.airportmanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode(callSuper = false)
public class APIResponse<T>  {
    private Boolean success;
    private final T data;
    private final String message;
    private String error;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static <E> APIResponse<E> success(E data, String message) {
        APIResponse<E> response = new APIResponse<>(data, message);
        response.setSuccess(true);
        return response;
    }

    public static <E> APIResponse<E> success(E data) {
        return APIResponse.success(data, "Task successfully.");
    }

    public static APIResponse<Void> success(String message) {
        return APIResponse.success(null, message);
    }

    public static <E> APIResponse<E> error(String message, String type, E data) {
        APIResponse<E> response = new APIResponse<>(data, message);
        response.setError(type);
        return response;
    }

    public static APIResponse<Void> error(String message, String type) {
        return APIResponse.error(message, type, null);
    }
}
