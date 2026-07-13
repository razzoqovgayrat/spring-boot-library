package com.library.exception;

public class AppBadRequestException extends RuntimeException {
    public AppBadRequestException(String invalidToken) {
        super(invalidToken);
    }
}
