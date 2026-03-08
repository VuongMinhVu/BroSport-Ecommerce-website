package com.se2.demo.utils.exception;

public class StockExistingException extends RuntimeException {
    public StockExistingException(String message) {
        super(message);
    }
}
