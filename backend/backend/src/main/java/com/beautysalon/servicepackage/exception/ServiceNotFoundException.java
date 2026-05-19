package com.beautysalon.servicepackage.exception;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
