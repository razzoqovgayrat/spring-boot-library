package com.library.exception;

import com.library.dto.response.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Void> handleAppBadRequestException(ResourceNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ApiResponse<Void> handleMemberNotFoundException(MemberNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ApiResponse<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(AppBadRequestException.class)
    public ApiResponse<Void> handleAppBadRequestException(AppBadRequestException e) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserBlockedException.class)
    public ApiResponse<Void> handleUserBlocked(UserBlockedException e) {
        return ApiResponse.error(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ApiResponse<Void> handleDuplicateResource(DuplicateResourceException e) {
        return ApiResponse.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ApiResponse<Void> handleConflictException(ConflictException e) {
        return ApiResponse.error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ApiResponse<Void> handleInvalidCredentials(InvalidCredentialsException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException e) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponse.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ApiResponse<Void> handleOptimisticLoc(OptimisticLockException ex) {
        return ApiResponse.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    public ApiResponse<Void> handleLockedAccount(LockedException e) {
        return ApiResponse.error(HttpStatus.LOCKED, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Void> handleBadCredentials(BadCredentialsException e) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<Void> handleValidationErrors(MethodArgumentNotValidException e) {
        List<String> validationErrors = new LinkedList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            validationErrors.add(fieldError.getDefaultMessage());
        }

        return ApiResponse.error(validationErrors, HttpStatus.BAD_REQUEST, "Validation Failed");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGenericException(Exception e) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + e.getMessage());
    }
}
