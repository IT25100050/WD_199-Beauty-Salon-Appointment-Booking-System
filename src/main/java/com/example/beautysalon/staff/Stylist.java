package com.project.beautysalon.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stylists")

public class Stylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required.")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Column(nullable = false)
    private String lastName;

    private String email;

    private String phone;

    private String status;          // "active" | "inactive"

    private String shiftStart;      // "HH:mm" e.g. "09:00"

    private String shiftEnd;        // "HH:mm" e.g. "18:00"

    private Integer maxBookingsPerDay;

    // ✅ List<Boolean> — 7 entries (0=Monday ... 6=Sunday)
    // AppointmentService uses: stylist.getWeeklySchedule().get(dayOfWeek)
    // Stored as comma-separated string in DB via BooleanListConverter
    //@Convert(converter = BooleanListConverter.class)
    @Column(name = "weekly_schedule")
    private List<Boolean> weeklySchedule;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Stylist fromFileString(String line) {
        return null;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int toFileString() {
        return 0;
    }

    public String getType() {
        return "";
    }

    public void setName(String name) {
    }

    public void setSpecialization(String specialization) {
    }
}
