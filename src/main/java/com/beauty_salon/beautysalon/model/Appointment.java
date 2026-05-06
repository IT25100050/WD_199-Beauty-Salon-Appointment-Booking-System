package com.beauty_salon.beautysalon.model;

import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments",
        indexes = {
                @Index(name = "idx_appt_client", columnList = "client_id"),
                @Index(name = "idx_appt_stylist", columnList = "stylist_id"),
                @Index(name = "idx_appt_start", columnList = "start_time"),
                @Index(name = "idx_appt_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stylist_id", nullable = false)
    private Stylist stylist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;

    @NotNull(message = "Start time is required")
    @Future(message = "Appointment must be in the future")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /** Computed from startTime + service.durationMinutes */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    /** Actual price charged (may differ from base price for promotions). */
    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountCharged;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountApplied = BigDecimal.ZERO;

    /** Free-text notes from the client (e.g. allergies, preferences). */
    @Size(max = 1000)
    @Column(length = 1000)
    private String clientNotes;

    /** Internal notes from the stylist or admin. */
    @Size(max = 1000)
    @Column(length = 1000)
    private String internalNotes;

    /** Whether the client was sent a reminder notification. */
    @Column(nullable = false)
    @Builder.Default
    private boolean reminderSent = false;

    /** Client rating after the appointment (1–5). */
    @Min(1) @Max(5)
    private Integer clientRating;

    @Size(max = 500)
    @Column(length = 500)
    private String clientReview;

    /** Timestamp when the appointment was completed or cancelled. */
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Helpers ─────────────────────────────────────────────────

    @PrePersist
    @PreUpdate
    private void computeEndTime() {
        if (startTime != null && service != null) {
            this.endTime = startTime.plusMinutes(service.getDurationMinutes());
        }
    }

    public boolean isActive() {
        return status == AppointmentStatus.PENDING
                || status == AppointmentStatus.CONFIRMED
                || status == AppointmentStatus.IN_PROGRESS;
    }

    public boolean isCancellable() {
        return status == AppointmentStatus.PENDING
                || status == AppointmentStatus.CONFIRMED;
    }
}
