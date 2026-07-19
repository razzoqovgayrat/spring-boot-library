package com.library.exception;

import lombok.Getter;

/**
 * Talabnomadagi 5-bo'lim (biznes qoidalar) buzilganda tashlanadi.
 * errorCode -> ErrorResponse.code (masalan "COPY_NOT_AVAILABLE",
 * "MEMBER_LOAN_LIMIT_EXCEEDED") GlobalExceptionHandler orqali 409 qaytaradi.
 */
@Getter
public class BusinessRuleViolationException extends RuntimeException {

    private final String errorCode;

    public BusinessRuleViolationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}