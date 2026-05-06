package com.beauty_salon.beautysalon.service;

import com.beauty_salon.beautysalon.dto.ServiceRequest;
import com.beauty_salon.beautysalon.dto.ServiceResponse;
import com.beauty_salon.beautysalon.enums.ServiceCategory;
import com.beauty_salon.beautysalon.exception.*;
import com.beauty_salon.beautysalon.model.SalonService;
import com.beauty_salon.beautysalon.model.Stylist;
import com.beauty_salon.beautysalon.repository.ServiceRepository;
import com.beauty_salon.beautysalon.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;

    // ── Create ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        log.info("Creating service: {}", request.getName());

        if (serviceRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateServiceException(request.getName());
        }

        validatePriceRange(request);

        SalonService service = SalonService.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .maxPrice(request.getMaxPrice())
                .durationMinutes(request.getDurationMinutes())
                .imageUrl(request.getImageUrl())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        SalonService saved = serviceRepository.save(service);
        log.info("Service created with id={}", saved.getId());
        return toResponse(saved);
    }

    // ── Read ─────────────────────────────────────────────────────

    @Override
    public ServiceResponse getServiceById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public Page<ServiceResponse> getAllActiveServices(Pageable pageable) {
        return serviceRepository.findAllByActiveTrue(pageable).map(this::toResponse);
    }

    @Override
    public Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable) {
        return serviceRepository.findAllByCategoryAndActiveTrue(category, pageable).map(this::toResponse);
    }

    @Override
    public Page<ServiceResponse> searchServices(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return getAllActiveServices(pageable);
        }
        return serviceRepository.searchByKeyword(keyword.trim(), pageable).map(this::toResponse);
    }

    @Override
    public List<ServiceResponse> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = new BigDecimal("9999");
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidAppointmentException("minPrice cannot be greater than maxPrice");
        }
        return serviceRepository.findByPriceRange(minPrice, maxPrice)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Update ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        log.info("Updating service id={}", id);
        SalonService service = findOrThrow(id);

        // Name uniqueness check (ignore same service)
        if (!service.getName().equalsIgnoreCase(request.getName())
                && serviceRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateServiceException(request.getName());
        }

        validatePriceRange(request);

        service.setName(request.getName().trim());
        service.setDescription(request.getDescription());
        service.setCategory(request.getCategory());
        service.setPrice(request.getPrice());
        service.setMaxPrice(request.getMaxPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        if (request.getImageUrl() != null) service.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) service.setActive(request.getActive());

        return toResponse(serviceRepository.save(service));
    }

    @Override
    @Transactional
    public ServiceResponse toggleServiceStatus(Long id) {
        SalonService service = findOrThrow(id);
        service.setActive(!service.isActive());
        log.info("Service id={} active set to {}", id, service.isActive());
        return toResponse(serviceRepository.save(service));
    }

    // ── Delete ───────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteService(Long id) {
        SalonService service = findOrThrow(id);

        boolean hasUpcoming = service.getAppointments().stream()
                .anyMatch(a -> a.isActive());

        if (hasUpcoming) {
            throw new InvalidAppointmentException(
                    "Cannot delete service '" + service.getName() +
                            "' — it has upcoming active appointments. Deactivate it instead.");
        }

        serviceRepository.delete(service);
        log.info("Service id={} deleted", id);
    }

    // ── Queries ──────────────────────────────────────────────────

    @Override
    public List<ServiceResponse> getTopServicesByBookings(int limit) {
        return serviceRepository.findTopByBookings(PageRequest.of(0, limit))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getTopServicesByRating(int limit) {
        return serviceRepository.findTopByRating(PageRequest.of(0, limit))
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getServicesForStylist(Long stylistId) {
        return serviceRepository.findByStylistId(stylistId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Statistics ───────────────────────────────────────────────

    @Override
    public Map<ServiceCategory, Long> getServiceCountByCategory() {
        return Arrays.stream(ServiceCategory.values())
                .collect(Collectors.toMap(
                        cat -> cat,
                        serviceRepository::countByCategory,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    // ── Private helpers ──────────────────────────────────────────

    private SalonService findOrThrow(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    private void validatePriceRange(ServiceRequest req) {
        if (req.getMaxPrice() != null
                && req.getMaxPrice().compareTo(req.getPrice()) < 0) {
            throw new InvalidAppointmentException(
                    "maxPrice (" + req.getMaxPrice() + ") cannot be less than price (" + req.getPrice() + ")");
        }
    }

    // ── Mapper ───────────────────────────────────────────────────

    private ServiceResponse toResponse(SalonService s) {
        List<ServiceResponse.StylistSummary> stylistSummaries = s.getAvailableStylists()
                .stream()
                .filter(Stylist::isActive)
                .map(st -> ServiceResponse.StylistSummary.builder()
                        .id(st.getId())
                        .fullName(st.getFullName())
                        .role(st.getRole())
                        .photoUrl(st.getPhotoUrl())
                        .averageRating(st.getAverageRating())
                        .currentlyBusy(st.isCurrentlyBusy())
                        .build())
                .collect(Collectors.toList());

        return ServiceResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .category(s.getCategory())
                .price(s.getPrice())
                .maxPrice(s.getMaxPrice())
                .formattedPriceRange(s.getFormattedPriceRange())
                .durationMinutes(s.getDurationMinutes())
                .formattedDuration(s.getFormattedDuration())
                .active(s.isActive())
                .averageRating(s.getAverageRating())
                .totalReviews(s.getTotalReviews())
                .totalBookings(s.getTotalBookings())
                .imageUrl(s.getImageUrl())
                .availableStylists(stylistSummaries)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}