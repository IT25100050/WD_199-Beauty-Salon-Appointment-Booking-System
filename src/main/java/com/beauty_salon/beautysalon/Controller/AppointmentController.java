package com.beauty_salon.beautysalon.Controller;

import com.beauty_salon.beautysalon.dto.AppointmentRequest;
import com.beauty_salon.beautysalon.dto.AppointmentResponse;
import com.beauty_salon.beautysalon.dto.ReviewRequest;
import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import com.beauty_salon.beautysalon.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST API for appointment booking and management.
 *
 * Base path: /api/v1/appointments
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // ── POST /api/v1/appointments ────────────────────────────────
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(request));
    }

    // ── GET /api/v1/appointments/{id} ───────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // ── GET /api/v1/appointments ─────────────────────────────────
    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime"));

        Page<AppointmentResponse> result = (status != null)
                ? appointmentService.getAppointmentsByStatus(status, pageable)
                : appointmentService.getAllAppointments(pageable);

        return ResponseEntity.ok(result);
    }

    // ── GET /api/v1/appointments/today ───────────────────────────
    @GetMapping("/today")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments() {
        return ResponseEntity.ok(appointmentService.getTodayAppointments());
    }

    // ── GET /api/v1/appointments/date-range ──────────────────────
    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(from, to));
    }

    // ── GET /api/v1/appointments/client/{clientId} ───────────────
    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<AppointmentResponse>> getByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        return ResponseEntity.ok(appointmentService.getAppointmentsByClient(clientId, pageable));
    }

    // ── GET /api/v1/appointments/stylist/{stylistId}/schedule ────
    @GetMapping("/stylist/{stylistId}/schedule")
    public ResponseEntity<List<AppointmentResponse>> getStylistSchedule(
            @PathVariable Long stylistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getStylistSchedule(stylistId, date));
    }

    // ── PATCH /api/v1/appointments/{id}/confirm ──────────────────
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    // ── PATCH /api/v1/appointments/{id}/start ────────────────────
    @PatchMapping("/{id}/start")
    public ResponseEntity<AppointmentResponse> start(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.startAppointment(id));
    }

    // ── PATCH /api/v1/appointments/{id}/complete ─────────────────
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    // ── PATCH /api/v1/appointments/{id}/cancel ───────────────────
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason));
    }

    // ── PATCH /api/v1/appointments/{id}/no-show ──────────────────
    @PatchMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }

    // ── PATCH /api/v1/appointments/{id}/reschedule ───────────────
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, newStartTime));
    }

    // ── POST /api/v1/appointments/{id}/review ────────────────────
    @PostMapping("/{id}/review")
    public ResponseEntity<AppointmentResponse> submitReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest review) {
        return ResponseEntity.ok(appointmentService.submitReview(id, review));
    }

    // ── POST /api/v1/appointments/reminders/send ─────────────────
    @PostMapping("/reminders/send")
    public ResponseEntity<Map<String, Integer>> sendReminders(
            @RequestParam(defaultValue = "24") int hoursAhead) {
        int sent = appointmentService.sendUpcomingReminders(hoursAhead);
        return ResponseEntity.ok(Map.of("remindersSent", sent));
    }

    // ── GET /api/v1/appointments/analytics/revenue ───────────────
    @GetMapping("/analytics/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        BigDecimal revenue = appointmentService.getTotalRevenue(from, to);
        long count         = appointmentService.countCompletedAppointments(from, to);

        return ResponseEntity.ok(Map.of(
                "from", from,
                "to", to,
                "totalRevenue", revenue,
                "completedAppointments", count,
                "averageTicket", count > 0
                        ? revenue.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO
        ));
    }

    // ── GET /api/v1/appointments/analytics/top-services ──────────
    @GetMapping("/analytics/top-services")
    public ResponseEntity<Map<String, Long>> getTopServices(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(appointmentService.getTopServicesByBookingCount(limit));
    }
}