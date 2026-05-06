package com.beauty_salon.beautysalon.dto;

import com.beauty_salon.beautysalon.enums.ServiceCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO returned to the client for SalonService data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private Long id;
    private String name;
    private String description;
    private ServiceCategory category;

    private BigDecimal price;
    private BigDecimal maxPrice;
    private String formattedPriceRange;   // e.g. "$240–$340"

    private Integer durationMinutes;
    private String formattedDuration;     // e.g. "2h 30m"

    private boolean active;
    private BigDecimal averageRating;
    private int totalReviews;
    private int totalBookings;

    private String imageUrl;

    /** Summary list of stylists who can perform this service. */
    private List<StylistSummary> availableStylists;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StylistSummary {
        private Long id;
        private String fullName;
        private String role;
        private String photoUrl;
        private BigDecimal averageRating;
        private boolean currentlyBusy;
    }
}
