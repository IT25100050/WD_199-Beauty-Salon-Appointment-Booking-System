package com.project.beautysalon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * StylistDTO
 * Data Transfer Object for salon staff / stylist profiles.
 * Used in both request bodies and API responses.
 *
 * Mapped entity: Stylist.java  (or Staff.java)
 * Controller   : StaffController.java
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class StylistDTO {

    // ── Identity ──────────────────────────────────────────────────────────────

    /** Auto-generated primary key. Null on create request, populated in response. */
    private Long id;

    // ── Personal info ─────────────────────────────────────────────────────────

    /**
     * Staff member's first name.
     */
    @NotBlank(message = "First name is required.")
    @Size(max = 60, message = "First name must not exceed 60 characters.")
    private String firstName;

    /**
     * Staff member's last name.
     */
    @NotBlank(message = "Last name is required.")
    @Size(max = 60, message = "Last name must not exceed 60 characters.")
    private String lastName;

    /**
     * Work email address. Must be unique across all staff.
     */
    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    @Size(max = 120, message = "Email must not exceed 120 characters.")
    private String email;

    /**
     * Contact phone number.
     */
    @Pattern(regexp = "^[+]?[0-9\\s\\-().]{7,20}$",
            message = "Please provide a valid phone number.")
    private String phone;

    /**
     * Short professional bio shown on the stylist's profile card and booking page.
     */
    @Size(max = 500, message = "Bio must not exceed 500 characters.")
    private String bio;

    /**
     * URL of the stylist's profile photo.
     * If null, the frontend renders initials inside a coloured avatar.
     */
    private String photoUrl;

    // ── Role & employment ─────────────────────────────────────────────────────

    /**
     * Job role / title.
     * Values: "Senior Stylist" | "Stylist" | "Nail Technician" |
     *         "Esthetician" | "Massage Therapist" | "Makeup Artist"
     */
    @NotBlank(message = "Role is required.")
    @Size(max = 60, message = "Role must not exceed 60 characters.")
    private String role;

    /**
     * Date the staff member joined the salon.
     */
    private LocalDate startDate;

    /**
     * Commission percentage applied to the stylist's bookings.
     * Example: 40 means they receive 40% of the service price.
     */
    @Min(value = 0,   message = "Commission cannot be negative.")
    @Max(value = 100, message = "Commission cannot exceed 100%.")
    private Integer commissionPercent;

    /**
     * Maximum number of appointments the stylist can take per day.
     * Defaults to 8 if not specified.
     */
    @Min(value = 1,  message = "Max bookings per day must be at least 1.")
    @Max(value = 30, message = "Max bookings per day cannot exceed 30.")
    private Integer maxBookingsPerDay;

    // ── Status ────────────────────────────────────────────────────────────────

    /**
     * Current employment status.
     * Values: "active" | "inactive" | "on-leave"
     */
    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "active|inactive|on-leave",
            message = "Status must be 'active', 'inactive', or 'on-leave'.")
    private String status;

    // ── Specialties ───────────────────────────────────────────────────────────

    /**
     * List of services / techniques the stylist specialises in.
     * Example: ["Balayage", "Colour", "Cuts", "Extensions"]
     * These are matched against ServiceDTO.name when building booking dropdowns.
     */
    private List<String> specialties;

    /**
     * IDs of Service entities this stylist is assigned to perform.
     * Used to populate the service → stylist mapping in the booking flow.
     */
    private List<Long> serviceIds;

    // ── Weekly schedule ───────────────────────────────────────────────────────

    /**
     * Working days as a 7-element boolean list.
     * Index: 0 = Monday … 6 = Sunday
     * Example: [true, true, true, true, true, false, false]
     *          → Monday to Friday, off on weekends
     */
    @Size(min = 7, max = 7, message = "Schedule must contain exactly 7 days.")
    private List<Boolean> weeklySchedule;

    /**
     * Default shift start time (HH:mm format).
     * Example: "09:00"
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Shift start must be in HH:mm format.")
    private String shiftStart;

    /**
     * Default shift end time (HH:mm format).
     * Example: "18:00"
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "Shift end must be in HH:mm format.")
    private String shiftEnd;

    // ── UI / theme ────────────────────────────────────────────────────────────

    /**
     * CSS class name for the avatar / banner colour theme.
     * Values: banner-hair | banner-skin | banner-nails |
     *         banner-massage | banner-makeup | banner-multi
     */
    private String colorTheme;

    // ── Performance stats (response only) ─────────────────────────────────────

    /**
     * Total completed appointments across all time.
     */
    private Integer totalBookings;

    /**
     * Total number of distinct clients served.
     */
    private Integer totalClients;

    /**
     * Average customer rating for this stylist (1.0 – 5.0).
     */
    private Double averageRating;

    /**
     * Total revenue generated by this stylist (before commission split).
     */
    private BigDecimal totalRevenue;

    // ── Linked user account ───────────────────────────────────────────────────

    /**
     * ID of the linked User account (if the stylist has login access).
     * Null for stylists who do not have a system login.
     */
    private Long userId;

    // ── Audit ─────────────────────────────────────────────────────────────────

    /**
     * Timestamp when the record was created (response only).
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update (response only).
     */
    private LocalDateTime updatedAt;


}
