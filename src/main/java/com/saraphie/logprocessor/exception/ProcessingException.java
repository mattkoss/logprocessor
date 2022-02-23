package com.saraphie.logprocessor.exception;

public class ProcessingException extends RuntimeException {
    public ProcessingException(String message, Throwable ex) {
        super(message, ex);
    }
}
