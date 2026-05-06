package com.beauty_salon.beautysalon.service;

import com.beauty_salon.beautysalon.dto.ServiceRequest;
import com.beauty_salon.beautysalon.dto.ServiceResponse;
import com.beauty_salon.beautysalon.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core contract for managing salon services (CRUD + queries).
 */
public interface ServiceManagementService {

    // ── CRUD ─────────────────────────────────────────────────────

    /**
     * Creates a new salon service.
     *
     * @param request validated service data
     * @return the persisted service as a response DTO
     * @throws com.beautysalon.exception.DuplicateServiceException if name already exists
     */
    ServiceResponse createService(ServiceRequest request);

    /**
     * Returns a single service by ID.
     *
     * @throws com.beautysalon.exception.ServiceNotFoundException if not found
     */
    ServiceResponse getServiceById(Long id);

    /**
     * Returns all active services (paginated).
     */
    Page<ServiceResponse> getAllActiveServices(Pageable pageable);

    /**
     * Returns all active services in a specific category (paginated).
     */
    Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable);

    /**
     * Full-text search across service name and description.
     */
    Page<ServiceResponse> searchServices(String keyword, Pageable pageable);

    /**
     * Returns services within a price range.
     */
    List<ServiceResponse> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Updates an existing service.
     *
     * @throws com.beautysalonn.exception.ServiceNotFoundException if not found
     * @throws com.beautysalon.exception.DuplicateServiceException if new name conflicts
     */
    ServiceResponse updateService(Long id, ServiceRequest request);

    /**
     * Toggles the active/inactive status of a service.
     */
    ServiceResponse toggleServiceStatus(Long id);

    /**
     * Permanently deletes a service.
     * Only allowed if the service has no upcoming appointments.
     *
     * @throws com.beautysalon.exception.InvalidAppointmentException if upcoming bookings exist
     */
    void deleteService(Long id);

    // ── Queries ──────────────────────────────────────────────────

    /**
     * Returns the top N most booked services.
     */
    List<ServiceResponse> getTopServicesByBookings(int limit);

    /**
     * Returns the top N highest-rated services.
     */
    List<ServiceResponse> getTopServicesByRating(int limit);

    /**
     * Returns all services a specific stylist can perform.
     */
    List<ServiceResponse> getServicesForStylist(Long stylistId);

    // ── Statistics ───────────────────────────────────────────────

    /**
     * Returns a summary count of active services per category.
     */
    java.util.Map<ServiceCategory, Long> getServiceCountByCategory();
}
