package com.beauty_salon.beautysalon.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for booking a new appointment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Stylist ID is required")
    private Long stylistId;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be scheduled in the future")
    private LocalDateTime startTime;

    @Size(max = 1000)
    private String clientNotes;

    /** Optional coupon/promo code. */
    private String promoCode;
}
