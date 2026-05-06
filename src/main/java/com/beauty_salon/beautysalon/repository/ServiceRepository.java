package com.beauty_salon.beautysalon.repository;

import com.beauty_salon.beautysalon.enums.ServiceCategory;
import com.beauty_salon.beautysalon.model.SalonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<SalonService, Long> {

    Optional<SalonService> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<SalonService> findAllByActiveTrue();

    List<SalonService> findAllByCategoryAndActiveTrue(ServiceCategory category);

    Page<SalonService> findAllByActiveTrue(Pageable pageable);

    Page<SalonService> findAllByCategoryAndActiveTrue(ServiceCategory category, Pageable pageable);

    /** Full-text search on name and description. */
    @Query("SELECT s FROM SalonService s WHERE s.active = true AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SalonService> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** Services within a price range. */
    @Query("SELECT s FROM SalonService s WHERE s.active = true " +
            "AND s.price >= :minPrice AND s.price <= :maxPrice")
    List<SalonService> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice);

    /** Services that a specific stylist can perform. */
    @Query("SELECT s FROM SalonService s JOIN s.availableStylists st WHERE st.id = :stylistId AND s.active = true")
    List<SalonService> findByStylistId(@Param("stylistId") Long stylistId);

    /** Top N services by booking count. */
    @Query("SELECT s FROM SalonService s WHERE s.active = true ORDER BY s.totalBookings DESC")
    List<SalonService> findTopByBookings(Pageable pageable);

    /** Top N services by average rating. */
    @Query("SELECT s FROM SalonService s WHERE s.active = true AND s.totalReviews > 0 ORDER BY s.averageRating DESC")
    List<SalonService> findTopByRating(Pageable pageable);

    /** Toggle active status. */
    @Modifying
    @Query("UPDATE SalonService s SET s.active = :active WHERE s.id = :id")
    int setActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    /** Increment booking counter. */
    @Modifying
    @Query("UPDATE SalonService s SET s.totalBookings = s.totalBookings + 1 WHERE s.id = :id")
    void incrementBookingCount(@Param("id") Long id);

    long countByCategory(ServiceCategory category);

    long countByActiveTrue();
}
