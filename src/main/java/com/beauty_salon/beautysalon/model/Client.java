package com.beauty_salon.beautysalon.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 60)
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 60)
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number")
    @Column(length = 20)
    private String phone;

    private LocalDate dateOfBirth;

    @Size(max = 1000)
    @Column(length = 1000)
    private String notes;

    /** Whether this client qualifies as VIP (e.g. lifetime spend > $2000). */
    @Column(nullable = false)
    @Builder.Default
    private boolean vip = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Cumulative spend across all completed appointments. */
    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpend = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int totalVisits = 0;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Helpers ─────────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addSpend(BigDecimal amount) {
        this.totalSpend = this.totalSpend.add(amount);
        this.totalVisits++;
        evaluateVipStatus();
    }

    private void evaluateVipStatus() {
        if (this.totalSpend.compareTo(new BigDecimal("2000")) >= 0 || this.totalVisits >= 20) {
            this.vip = true;
        }
    }
}
