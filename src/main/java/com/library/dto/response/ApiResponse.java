package com.library.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(

        HttpStatus status,

        @Schema(example = "true")
        boolean success,

        @Schema(example = "Muvaffaqiyatli")
        String message,

        T data,

        @Schema(example = "2026-07-16T10:00:00Z")
        Instant timestamp,

        List<String> errors
) {

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(List<String> errors, HttpStatus status, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(status)
                .errors(errors)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}