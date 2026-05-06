package com.beauty_salon.beautysalon.repository;


import com.beauty_salon.beautysalon.enums.AppointmentStatus;
import com.beauty_salon.beautysalon.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ── By client ────────────────────────────────────────────────
    Page<Appointment> findByClientId(Long clientId, Pageable pageable);

    List<Appointment> findByClientIdAndStatus(Long clientId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.client.id = :clientId " +
            "AND a.startTime >= :from AND a.startTime <= :to ORDER BY a.startTime ASC")
    List<Appointment> findByClientIdAndDateRange(@Param("clientId") Long clientId,
                                                 @Param("from") LocalDateTime from,
                                                 @Param("to") LocalDateTime to);

    // ── By stylist ───────────────────────────────────────────────
    List<Appointment> findByStylistIdAndStatus(Long stylistId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.stylist.id = :stylistId " +
            "AND a.startTime >= :from AND a.startTime < :to ORDER BY a.startTime ASC")
    List<Appointment> findByStylistSchedule(@Param("stylistId") Long stylistId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    // ── Conflict detection ───────────────────────────────────────
    /**
     * Checks if the stylist has an overlapping booking in the proposed window.
     * Excludes appointments with a specific ID (for update scenarios).
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.stylist.id = :stylistId " +
            "AND a.id <> :excludeId " +
            "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasStylistConflict(@Param("stylistId") Long stylistId,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime,
                               @Param("excludeId") Long excludeId);

    /**
     * Checks if the client already has a booking in the proposed window.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.client.id = :clientId " +
            "AND a.id <> :excludeId " +
            "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasClientConflict(@Param("clientId") Long clientId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("excludeId") Long excludeId);

    // ── By status ────────────────────────────────────────────────
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    List<Appointment> findByStatusIn(List<AppointmentStatus> statuses);

    // ── Date-range queries ───────────────────────────────────────
    @Query("SELECT a FROM Appointment a WHERE a.startTime >= :from AND a.startTime < :to " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByDateRange(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);

    @Query("SELECT a FROM Appointment a WHERE a.startTime >= :from AND a.startTime < :to " +
            "AND a.status = :status ORDER BY a.startTime ASC")
    List<Appointment> findByDateRangeAndStatus(@Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to,
                                               @Param("status") AppointmentStatus status);

    // ── Reminders ────────────────────────────────────────────────
    @Query("SELECT a FROM Appointment a WHERE a.reminderSent = false " +
            "AND a.status = 'CONFIRMED' " +
            "AND a.startTime BETWEEN :from AND :to")
    List<Appointment> findPendingReminders(@Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    @Modifying
    @Query("UPDATE Appointment a SET a.reminderSent = true WHERE a.id IN :ids")
    void markRemindersAsSent(@Param("ids") List<Long> ids);

    // ── Status update ────────────────────────────────────────────
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status, a.resolvedAt = :resolvedAt WHERE a.id = :id")
    int updateStatus(@Param("id") Long id,
                     @Param("status") AppointmentStatus status,
                     @Param("resolvedAt") LocalDateTime resolvedAt);

    // ── Analytics ────────────────────────────────────────────────
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = 'COMPLETED' " +
            "AND a.startTime >= :from AND a.startTime < :to")
    long countCompletedInRange(@Param("from") LocalDateTime from,
                               @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(a.amountCharged), 0) FROM Appointment a " +
            "WHERE a.status = 'COMPLETED' AND a.startTime >= :from AND a.startTime < :to")
    java.math.BigDecimal sumRevenueInRange(@Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to);

    @Query("SELECT a.service.id, a.service.name, COUNT(a) AS cnt FROM Appointment a " +
            "WHERE a.status = 'COMPLETED' GROUP BY a.service.id, a.service.name ORDER BY cnt DESC")
    List<Object[]> findTopServicesByCompletedCount(Pageable pageable);
}
