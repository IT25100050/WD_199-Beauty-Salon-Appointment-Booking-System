package com.beautysalon.inventory.exception;

public class LowStockException extends RuntimeException {
    public LowStockException(String message) {
        super(message);
    }
}
