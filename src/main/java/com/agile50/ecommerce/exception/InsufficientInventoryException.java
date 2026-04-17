package com.agile50.ecommerce.exception;

public class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
