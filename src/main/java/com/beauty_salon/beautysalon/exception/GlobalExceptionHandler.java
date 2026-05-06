package com.beauty_salon.beautysalon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Payload ──────────────────────────────────────────────────

    record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> fieldErrors
    ) {}

    private ErrorResponse build(HttpStatus status, String message,
                                WebRequest request, Map<String, String> fieldErrors) {
        String path = request.getDescription(false).replace("uri=", "");
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), message, path, fieldErrors);
    }

    // ── 404 ──────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null));
    }

    // ── 409 ──────────────────────────────────────────────────────

    @ExceptionHandler({SchedulingConflictException.class, DuplicateServiceException.class})
    public ResponseEntity<ErrorResponse> handleConflict(SalonException ex, WebRequest req) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, ex.getMessage(), req, null));
    }

    // ── 400 ──────────────────────────────────────────────────────

    @ExceptionHandler({InvalidAppointmentException.class,
            StylistNotQualifiedException.class,
            AppointmentNotCancellableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(SalonException ex, WebRequest req) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null));
    }

    /** Bean validation errors — returns field-level detail. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        (a, b) -> a));
        log.warn("Validation failed: {}", fieldErrors);
        return ResponseEntity.badRequest()
                .body(build(HttpStatus.BAD_REQUEST, "Validation failed", req, fieldErrors));
    }

    // ── 500 ──────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest req) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred", req, null));
    }
}
