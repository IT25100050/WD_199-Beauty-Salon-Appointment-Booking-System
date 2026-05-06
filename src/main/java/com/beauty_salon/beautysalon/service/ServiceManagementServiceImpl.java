package com.beauty_salon.beautysalon.service;

import com.beauty_salon.beautysalon.dto.AppointmentRequest;
import com.beauty_salon.beautysalon.dto.AppointmentResponse;
import com.beauty_salon.beautysalon.dto.ReviewRequest;
import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import com.beauty_salon.beautysalon.exception.*;
import com.beauty_salon.beautysalon.model.*;
import com.beauty_salon.beautysalon.repository.*;
import com.beauty_salon.beautysalon.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository     serviceRepository;
    private final ClientRepository      clientRepository;
    private final StylistRepository     stylistRepository;

    // Minimum hours before appointment when cancellation is still allowed
    private static final int CANCELLATION_CUTOFF_HOURS = 2;

    // ── Book ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        log.info("Booking appointment: client={} service={} stylist={} at {}",
                request.getClientId(), request.getServiceId(),
                request.getStylistId(), request.getStartTime());

        Client      client  = findClientOrThrow(request.getClientId());
        SalonService service = findServiceOrThrow(request.getServiceId());
        Stylist     stylist = findStylistOrThrow(request.getStylistId());

        validateServiceActive(service);
        validateStylistQualified(stylist, service);

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime   = startTime.plusMinutes(service.getDurationMinutes());

        validateNoClientConflict(client.getId(), startTime, endTime, null);
        validateNoStylistConflict(stylist.getId(), startTime, endTime, null);
        validateBusinessHours(startTime, endTime);

        BigDecimal price = applyPromo(service.getPrice(), request.getPromoCode());

        Appointment appt = Appointment.builder()
                .client(client)
                .service(service)
                .stylist(stylist)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .amountCharged(price)
                .discountApplied(service.getPrice().subtract(price))
                .clientNotes(request.getClientNotes())
                .build();

        Appointment saved = appointmentRepository.save(appt);
        serviceRepository.incrementBookingCount(service.getId());

        log.info("Appointment created with id={}", saved.getId());
        return toResponse(saved);
    }

    // ── Status transitions ───────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(Long id) {
        Appointment appt = findOrThrow(id);
        requireStatus(appt, AppointmentStatus.PENDING);
        appt.setStatus(AppointmentStatus.CONFIRMED);
        log.info("Appointment id={} confirmed", id);
        return toResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional
    public AppointmentResponse startAppointment(Long id) {
        Appointment appt = findOrThrow(id);
        requireStatus(appt, AppointmentStatus.CONFIRMED);
        appt.setStatus(AppointmentStatus.IN_PROGRESS);
        appt.getStylist().setCurrentlyBusy(true);
        log.info("Appointment id={} started", id);
        return toResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appt = findOrThrow(id);
        requireStatus(appt, AppointmentStatus.IN_PROGRESS);

        appt.setStatus(AppointmentStatus.COMPLETED);
        appt.setResolvedAt(LocalDateTime.now());
        appt.getStylist().setCurrentlyBusy(false);
        appt.getStylist().setTotalAppointments(appt.getStylist().getTotalAppointments() + 1);

        // Credit the spend to the client's account
        appt.getClient().addSpend(appt.getAmountCharged());

        log.info("Appointment id={} completed — charged ${}", id, appt.getAmountCharged());
        return toResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(Long id, String reason) {
        Appointment appt = findOrThrow(id);

        if (!appt.isCancellable()) {
            throw new AppointmentNotCancellableException(id);
        }

        // Enforce cancellation cutoff
        long hoursUntilAppt = Duration.between(LocalDateTime.now(), appt.getStartTime()).toHours();
        if (hoursUntilAppt < CANCELLATION_CUTOFF_HOURS) {
            throw new InvalidAppointmentException(
                    "Appointments cannot be cancelled less than " +
                            CANCELLATION_CUTOFF_HOURS + " hours before the scheduled time.");
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        appt.setResolvedAt(LocalDateTime.now());
        if (reason != null && !reason.isBlank()) {
            appt.setInternalNotes("CANCELLED: " + reason);
        }

        log.info("Appointment id={} cancelled", id);
        return toResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional
    public AppointmentResponse markNoShow(Long id) {
        Appointment appt = findOrThrow(id);
        requireStatus(appt, AppointmentStatus.CONFIRMED);
        appt.setStatus(AppointmentStatus.NO_SHOW);
        appt.setResolvedAt(LocalDateTime.now());
        log.info("Appointment id={} marked NO_SHOW", id);
        return toResponse(appointmentRepository.save(appt));
    }

    @Override
    @Transactional
    public AppointmentResponse rescheduleAppointment(Long id, LocalDateTime newStartTime) {
        Appointment appt = findOrThrow(id);

        if (!appt.isCancellable()) {
            throw new InvalidAppointmentException(
                    "Appointment id=" + id + " cannot be rescheduled in its current state.");
        }

        if (newStartTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("New start time must be in the future.");
        }

        LocalDateTime newEnd = newStartTime.plusMinutes(appt.getService().getDurationMinutes());
        validateNoStylistConflict(appt.getStylist().getId(), newStartTime, newEnd, id);
        validateNoClientConflict(appt.getClient().getId(), newStartTime, newEnd, id);
        validateBusinessHours(newStartTime, newEnd);

        appt.setStartTime(newStartTime);
        appt.setEndTime(newEnd);
        appt.setStatus(AppointmentStatus.PENDING); // requires re-confirm

        log.info("Appointment id={} rescheduled to {}", id, newStartTime);
        return toResponse(appointmentRepository.save(appt));
    }

    // ── Reviews ──────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse submitReview(Long appointmentId, ReviewRequest review) {
        Appointment appt = findOrThrow(appointmentId);

        if (appt.getStatus() != AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentException("Reviews can only be submitted for completed appointments.");
        }
        if (appt.getClientRating() != null) {
            throw new InvalidAppointmentException("A review has already been submitted for appointment " + appointmentId);
        }

        BigDecimal rating = BigDecimal.valueOf(review.getRating());
        appt.setClientRating(review.getRating());
        appt.setClientReview(review.getReview());

        // Propagate to service and stylist
        appt.getService().updateRating(rating);
        appt.getStylist().updateRating(rating);

        log.info("Review submitted for appointment id={} — rating={}", appointmentId, rating);
        return toResponse(appointmentRepository.save(appt));
    }

    // ── Listing ──────────────────────────────────────────────────

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Page<AppointmentResponse> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Override
    public Page<AppointmentResponse> getAppointmentsByClient(Long clientId, Pageable pageable) {
        findClientOrThrow(clientId); // validate existence
        return appointmentRepository.findByClientId(clientId, pageable).map(this::toResponse);
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByClientAndStatus(Long clientId, AppointmentStatus status) {
        return appointmentRepository.findByClientIdAndStatus(clientId, status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getStylistSchedule(Long stylistId, LocalDate date) {
        findStylistOrThrow(stylistId);
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to   = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByStylistSchedule(stylistId, from, to)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDate from, LocalDate to) {
        return appointmentRepository.findByDateRange(from.atStartOfDay(), to.atTime(LocalTime.MAX))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getTodayAppointments() {
        return getAppointmentsByDateRange(LocalDate.now(), LocalDate.now());
    }

    // ── Reminders ────────────────────────────────────────────────

    @Override
    @Transactional
    public int sendUpcomingReminders(int hoursAhead) {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to   = from.plusHours(hoursAhead);

        List<Appointment> pending = appointmentRepository.findPendingReminders(from, to);
        if (pending.isEmpty()) return 0;

        List<Long> ids = pending.stream().map(Appointment::getId).collect(Collectors.toList());

        // ── Notification dispatch would go here ──
        // notificationService.sendBulkReminders(pending);

        appointmentRepository.markRemindersAsSent(ids);
        log.info("Sent {} appointment reminders", ids.size());
        return ids.size();
    }

    // ── Analytics ────────────────────────────────────────────────

    @Override
    public long countCompletedAppointments(LocalDate from, LocalDate to) {
        return appointmentRepository.countCompletedInRange(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDate from, LocalDate to) {
        return appointmentRepository.sumRevenueInRange(
                from.atStartOfDay(), to.atTime(LocalTime.MAX));
    }

    @Override
    public Map<String, Long> getTopServicesByBookingCount(int limit) {
        return appointmentRepository.findTopServicesByCompletedCount(PageRequest.of(0, limit))
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[1],
                        row -> (Long)   row[2],
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    // ── Validation helpers ───────────────────────────────────────

    private void validateServiceActive(SalonService service) {
        if (!service.isActive()) {
            throw new InvalidAppointmentException(
                    "Service '" + service.getName() + "' is not currently available.");
        }
    }

    private void validateStylistQualified(Stylist stylist, SalonService service) {
        if (!stylist.isActive()) {
            throw new InvalidAppointmentException("Stylist is not currently active.");
        }
        if (!stylist.canPerform(service)) {
            throw new StylistNotQualifiedException(stylist.getId(), service.getId());
        }
    }

    private void validateNoStylistConflict(Long stylistId,
                                           LocalDateTime start, LocalDateTime end,
                                           Long excludeId) {
        if (appointmentRepository.hasStylistConflict(
                stylistId, start, end, excludeId == null ? -1L : excludeId)) {
            throw new SchedulingConflictException(
                    "The selected stylist already has an appointment in this time slot.");
        }
    }

    private void validateNoClientConflict(Long clientId,
                                          LocalDateTime start, LocalDateTime end,
                                          Long excludeId) {
        if (appointmentRepository.hasClientConflict(
                clientId, start, end, excludeId == null ? -1L : excludeId)) {
            throw new SchedulingConflictException(
                    "The client already has an appointment in this time slot.");
        }
    }

    private void validateBusinessHours(LocalDateTime start, LocalDateTime end) {
        // Salon open 08:00–20:00, Mon–Sat
        DayOfWeek day = start.getDayOfWeek();
        if (day == DayOfWeek.SUNDAY) {
            throw new InvalidAppointmentException("The salon is closed on Sundays.");
        }
        LocalTime openTime  = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(20, 0);
        if (start.toLocalTime().isBefore(openTime)
                || end.toLocalTime().isAfter(closeTime)) {
            throw new InvalidAppointmentException(
                    "Appointments must be within salon hours: 08:00–20:00.");
        }
    }

    private BigDecimal applyPromo(BigDecimal price, String promoCode) {
        if (promoCode == null || promoCode.isBlank()) return price;
        // Placeholder — wire to a PromoService in production
        if ("WELCOME10".equalsIgnoreCase(promoCode)) {
            return price.multiply(new BigDecimal("0.90"));
        }
        if ("VIP20".equalsIgnoreCase(promoCode)) {
            return price.multiply(new BigDecimal("0.80"));
        }
        log.warn("Unknown promo code '{}' — no discount applied", promoCode);
        return price;
    }

    private void requireStatus(Appointment appt, AppointmentStatus expected) {
        if (appt.getStatus() != expected) {
            throw new InvalidAppointmentException(
                    "Expected appointment status " + expected +
                            " but was " + appt.getStatus() + " for id=" + appt.getId());
        }
    }

    // ── Entity finders ───────────────────────────────────────────

    private Appointment  findOrThrow(Long id)        { return appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id)); }
    private Client       findClientOrThrow(Long id)  { return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id)); }
    private SalonService findServiceOrThrow(Long id) { return serviceRepository.findById(id).orElseThrow(() -> new ServiceNotFoundException(id)); }
    private Stylist      findStylistOrThrow(Long id) { return stylistRepository.findById(id).orElseThrow(() -> new StylistNotFoundException(id)); }

    // ── Mapper ───────────────────────────────────────────────────

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .status(a.getStatus())
                .clientId(a.getClient().getId())
                .clientName(a.getClient().getFullName())
                .clientEmail(a.getClient().getEmail())
                .serviceId(a.getService().getId())
                .serviceName(a.getService().getName())
                .serviceCategory(a.getService().getCategory().name())
                .formattedDuration(a.getService().getFormattedDuration())
                .stylistId(a.getStylist().getId())
                .stylistName(a.getStylist().getFullName())
                .stylistRole(a.getStylist().getRole())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .amountCharged(a.getAmountCharged())
                .discountApplied(a.getDiscountApplied())
                .clientNotes(a.getClientNotes())
                .internalNotes(a.getInternalNotes())
                .clientRating(a.getClientRating())
                .clientReview(a.getClientReview())
                .reminderSent(a.isReminderSent())
                .createdAt(a.getCreatedAt())
                .resolvedAt(a.getResolvedAt())
                .build();
    }
}