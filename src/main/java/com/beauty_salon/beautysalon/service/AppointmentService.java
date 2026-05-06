package com.beauty_salon.beautysalon.service;

import com.beauty_salon.beautysalon.dto.AppointmentRequest;
import com.beauty_salon.beautysalon.dto.AppointmentResponse;
import com.beauty_salon.beautysalon.dto.ReviewRequest;
import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Core contract for booking and managing appointments.
 */
public interface AppointmentService {

    // ── Booking lifecycle ────────────────────────────────────────

    /**
     * Books a new appointment after validating availability and stylist qualification.
     *
     * @throws com.beautysalon.exception.SchedulingConflictException  if time slot is taken
     * @throws com.beautysalon.exception.StylistNotQualifiedException if stylist can't perform service
     * @throws com.beautysalon.InvalidAppointmentException  for other business rule violations
     */
    AppointmentResponse bookAppointment(AppointmentRequest request);

    /**
     * Returns a single appointment by ID.
     *
     * @throws com.beautysalon.exception.AppointmentNotFoundException
     */
    AppointmentResponse getAppointmentById(Long id);

    /**
     * Confirms a PENDING appointment.
     */
    AppointmentResponse confirmAppointment(Long id);

    /**
     * Marks appointment as IN_PROGRESS (client arrived).
     */
    AppointmentResponse startAppointment(Long id);

    /**
     * Marks appointment as COMPLETED and posts spend to client profile.
     */
    AppointmentResponse completeAppointment(Long id);

    /**
     * Cancels a PENDING or CONFIRMED appointment.
     *
     * @param reason optional cancellation reason stored in internalNotes
     * @throws com.beautysalon.exception.AppointmentNotCancellableException if status disallows it
     */
    AppointmentResponse cancelAppointment(Long id, String reason);

    /**
     * Marks a CONFIRMED appointment as NO_SHOW.
     */
    AppointmentResponse markNoShow(Long id);

    /**
     * Reschedules an appointment to a new time slot.
     *
     * @throws com.beautysalon.exception.SchedulingConflictException if new slot is taken
     */
    AppointmentResponse rescheduleAppointment(Long id, java.time.LocalDateTime newStartTime);

    // ── Reviews ──────────────────────────────────────────────────

    /**
     * Submits a client review for a COMPLETED appointment.
     * Propagates rating to both the service and the stylist.
     */
    AppointmentResponse submitReview(Long appointmentId, ReviewRequest review);

    // ── Listing & filtering ──────────────────────────────────────

    Page<AppointmentResponse> getAllAppointments(Pageable pageable);

    Page<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);

    Page<AppointmentResponse> getAppointmentsByClient(Long clientId, Pageable pageable);

    List<AppointmentResponse> getAppointmentsByClientAndStatus(Long clientId, AppointmentStatus status);

    List<AppointmentResponse> getStylistSchedule(Long stylistId, LocalDate date);

    List<AppointmentResponse> getAppointmentsByDateRange(LocalDate from, LocalDate to);

    List<AppointmentResponse> getTodayAppointments();

    // ── Reminders ────────────────────────────────────────────────

    /**
     * Finds appointments starting within the next {@code hoursAhead} hours
     * that haven't had a reminder sent, and triggers notifications.
     *
     * @return number of reminders dispatched
     */
    int sendUpcomingReminders(int hoursAhead);

    // ── Analytics ────────────────────────────────────────────────

    long countCompletedAppointments(LocalDate from, LocalDate to);

    BigDecimal getTotalRevenue(LocalDate from, LocalDate to);

    Map<String, Long> getTopServicesByBookingCount(int limit);
}
