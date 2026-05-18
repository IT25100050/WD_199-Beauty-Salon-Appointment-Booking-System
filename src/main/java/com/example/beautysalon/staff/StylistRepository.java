package com.project.beautysalon.repository;

import com.project.beautysalon.model.Stylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StylistRepository extends JpaRepository<Stylist, Long> {

    // ── Simple derived queries ──
    List<Stylist> findByStatus(String status);

    List<Stylist> findByStatusOrderByFirstNameAsc(String status);

    // FIX: Method has 2 params (status, search) — query also uses exactly 2 (:status, :search)
    // Rule: every @Param in the method signature MUST appear in the @Query,
    //       and every :placeholder in @Query MUST have a matching @Param.
    // 'role' was in @Param but NOT in the query → Spring crashed.
    // Solution: removed 'role' param entirely since Stylist has no role field.
    @Query("SELECT st FROM Stylist st WHERE " +
            "(:status IS NULL OR LOWER(st.status) = LOWER(:status)) AND " +
            "(:search IS NULL OR " +
            " LOWER(st.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(st.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            " LOWER(st.email)     LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY st.firstName ASC")
    List<Stylist> findWithFilters(@Param("status") String status,
                                  @Param("search") String search);

    // ── Used by AppointmentService.isAvailable() ──
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
            "a.stylist.id = :stylistId AND " +
            "a.appointmentDate = :date AND " +
            "a.status != 'CANCELLED'")
    long countAppointmentsByDateAndStylist(@Param("stylistId") Long stylistId,
                                           @Param("date") LocalDate date);
}