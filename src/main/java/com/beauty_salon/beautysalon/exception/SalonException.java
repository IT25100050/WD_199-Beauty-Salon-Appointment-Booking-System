package com.beauty_salon.beautysalon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// ── Base ──────────────────────────────────────────────────────────

public class SalonException extends RuntimeException {
    public SalonException(String message) { super(message); }
    public SalonException(String message, Throwable cause) { super(message, cause); }
}

// ── 404 Not Found ─────────────────────────────────────────────────

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends SalonException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceNotFoundException extends ResourceNotFoundException {
    public ServiceNotFoundException(Long id) { super("SalonService", id); }
    public ServiceNotFoundException(String name) { super("SalonService not found with name: " + name); }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppointmentNotFoundException extends ResourceNotFoundException {
    public AppointmentNotFoundException(Long id) { super("Appointment", id); }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFoundException extends ResourceNotFoundException {
    public ClientNotFoundException(Long id) { super("Client", id); }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StylistNotFoundException extends ResourceNotFoundException {
    public StylistNotFoundException(Long id) { super("Stylist", id); }
}

// ── 409 Conflict ──────────────────────────────────────────────────

@ResponseStatus(HttpStatus.CONFLICT)
public class SchedulingConflictException extends SalonException {
    public SchedulingConflictException(String message) { super(message); }
}

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateServiceException extends SalonException {
    public DuplicateServiceException(String name) {
        super("A service with the name '" + name + "' already exists.");
    }
}

// ── 400 Bad Request ───────────────────────────────────────────────

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAppointmentException extends SalonException {
    public InvalidAppointmentException(String message) { super(message); }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StylistNotQualifiedException extends SalonException {
    public StylistNotQualifiedException(Long stylistId, Long serviceId) {
        super("Stylist " + stylistId + " is not qualified to perform service " + serviceId);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppointmentNotCancellableException extends SalonException {
    public AppointmentNotCancellableException(Long id) {
        super("Appointment " + id + " cannot be cancelled in its current state.");
    }
}
