package com.beauty_salon.beautysalon.model;

import com.beauty_salon.beautysalon.enums.ServiceCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a beauty service offered by the salon.
 * e.g. Balayage, Hydra Facial, Gel Manicure, etc.
 */
@Entity
@Table(name = "salon_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 100, message = "Service name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceCategory category;

    /**
     * Base price of the service in USD.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Optional maximum price for range-based pricing (e.g. $240–$340).
     */
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 8, fraction = 2)
    @Column(precision = 10, scale = 2)
    private BigDecimal maxPrice;

    /**
     * Duration of the service in minutes.
     */
    @NotNull(message = "Duration is required")
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    @Column(nullable = false)
    private Integer durationMinutes;

    /**
     * Whether the service is currently offered / visible to clients.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Average rating out of 5 (derived, updated by review events).
     */
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int totalReviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private int totalBookings = 0;

    /**
     * Optional image URL for the service.
     */
    @Column(length = 500)
    private String imageUrl;

    @ManyToMany(mappedBy = "specializations")
    @Builder.Default
    private List<Stylist> availableStylists = new ArrayList<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Helper methods ──────────────────────────────────────────

    /**
     * Returns the formatted price range string, e.g. "$120" or "$240–$340".
     */
    public String getFormattedPriceRange() {
        if (maxPrice != null && maxPrice.compareTo(price) > 0) {
            return String.format("$%s–$%s",
                    price.stripTrailingZeros().toPlainString(),
                    maxPrice.stripTrailingZeros().toPlainString());
        }
        return "$" + price.stripTrailingZeros().toPlainString();
    }

    /**
     * Returns the duration in a human-readable format, e.g. "1h 30m".
     */
    public String getFormattedDuration() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        if (hours == 0) return minutes + "m";
        if (minutes == 0) return hours + "h";
        return hours + "h " + minutes + "m";
    }

    public void incrementBookings() {
        this.totalBookings++;
    }

    public void updateRating(BigDecimal newRating) {
        BigDecimal totalScore = this.averageRating
                .multiply(BigDecimal.valueOf(this.totalReviews))
                .add(newRating);
        this.totalReviews++;
        this.averageRating = totalScore.divide(
                BigDecimal.valueOf(this.totalReviews), 2,
                java.math.RoundingMode.HALF_UP);
    }
}
