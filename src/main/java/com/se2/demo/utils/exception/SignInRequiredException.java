package com.se2.demo.utils.exception;

public class SignInRequiredException extends RuntimeException {
    public SignInRequiredException(String message) {
        super(message);
    }
}
