package com.library.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String conflictException) {
        super(conflictException);
    }
}
