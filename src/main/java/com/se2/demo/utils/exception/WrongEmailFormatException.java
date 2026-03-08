package com.se2.demo.utils.exception;

public class WrongEmailFormatException extends RuntimeException {
    public WrongEmailFormatException(String message) {
        super(message);
    }
}
