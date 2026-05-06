package com.beauty_salon.beautysalon.Controller;

import com.beauty_salon.beautysalon.dto.ServiceRequest;
import com.beauty_salon.beautysalon.dto.ServiceResponse;
import com.beauty_salon.beautysalon.enums.ServiceCategory;
import com.beauty_salon.beautysalon.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST API for salon service management.
 *
 * Base path: /api/v1/services
 */
@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    // ── POST /api/v1/services ────────────────────────────────────
    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        ServiceResponse response = serviceManagementService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/v1/services/{id} ────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.getServiceById(id));
    }

    // ── GET /api/v1/services ─────────────────────────────────────
    @GetMapping
    public ResponseEntity<Page<ServiceResponse>> getAllServices(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        Page<ServiceResponse> result = (category != null)
                ? serviceManagementService.getServicesByCategory(category, pageable)
                : serviceManagementService.getAllActiveServices(pageable);

        return ResponseEntity.ok(result);
    }

    // ── GET /api/v1/services/search ──────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<Page<ServiceResponse>> searchServices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(serviceManagementService.searchServices(keyword, pageable));
    }

    // ── GET /api/v1/services/price-range ─────────────────────────
    @GetMapping("/price-range")
    public ResponseEntity<List<ServiceResponse>> getByPriceRange(
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max) {
        return ResponseEntity.ok(serviceManagementService.getServicesByPriceRange(min, max));
    }

    // ── GET /api/v1/services/top/bookings ────────────────────────
    @GetMapping("/top/bookings")
    public ResponseEntity<List<ServiceResponse>> getTopByBookings(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(serviceManagementService.getTopServicesByBookings(limit));
    }

    // ── GET /api/v1/services/top/rating ──────────────────────────
    @GetMapping("/top/rating")
    public ResponseEntity<List<ServiceResponse>> getTopByRating(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(serviceManagementService.getTopServicesByRating(limit));
    }

    // ── GET /api/v1/services/stylist/{stylistId} ─────────────────
    @GetMapping("/stylist/{stylistId}")
    public ResponseEntity<List<ServiceResponse>> getServicesForStylist(@PathVariable Long stylistId) {
        return ResponseEntity.ok(serviceManagementService.getServicesForStylist(stylistId));
    }

    // ── GET /api/v1/services/stats/by-category ───────────────────
    @GetMapping("/stats/by-category")
    public ResponseEntity<Map<ServiceCategory, Long>> getStatsByCategory() {
        return ResponseEntity.ok(serviceManagementService.getServiceCountByCategory());
    }

    // ── PUT /api/v1/services/{id} ────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(serviceManagementService.updateService(id, request));
    }

    // ── PATCH /api/v1/services/{id}/toggle ───────────────────────
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ServiceResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(serviceManagementService.toggleServiceStatus(id));
    }

    // ── DELETE /api/v1/services/{id} ─────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
