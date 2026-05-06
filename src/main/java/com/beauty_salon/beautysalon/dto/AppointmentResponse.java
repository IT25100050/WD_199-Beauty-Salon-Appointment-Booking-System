package com.beauty_salon.beautysalon.dto;

import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;
    private AppointmentStatus status;

    // Client info
    private Long clientId;
    private String clientName;
    private String clientEmail;

    // Service info
    private Long serviceId;
    private String serviceName;
    private String serviceCategory;
    private String formattedDuration;

    // Stylist info
    private Long stylistId;
    private String stylistName;
    private String stylistRole;

    // Timing
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Financials
    private BigDecimal amountCharged;
    private BigDecimal discountApplied;

    // Notes
    private String clientNotes;
    private String internalNotes;

    // Review
    private Integer clientRating;
    private String clientReview;

    private boolean reminderSent;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
