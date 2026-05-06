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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stylists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String role;  // e.g. "Senior Colorist", "Lash Technician"

    @Email
    @Column(unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    @Column(length = 500)
    private String photoUrl;

    /** Service categories the stylist is qualified for. */
    @ElementCollection(targetClass = ServiceCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "stylist_categories",
            joinColumns = @JoinColumn(name = "stylist_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<ServiceCategory> categories = new HashSet<>();

    /** Specific services the stylist performs. */
    @ManyToMany
    @JoinTable(name = "stylist_services",
            joinColumns = @JoinColumn(name = "stylist_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    @Builder.Default
    private List<SalonService> specializations = new ArrayList<>();

    @OneToMany(mappedBy = "stylist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** True when the stylist currently has a client in the chair. */
    @Column(nullable = false)
    @Builder.Default
    private boolean currentlyBusy = false;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int totalReviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private int totalAppointments = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Helpers ─────────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean canPerform(SalonService service) {
        return specializations.contains(service)
                || categories.contains(service.getCategory());
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
