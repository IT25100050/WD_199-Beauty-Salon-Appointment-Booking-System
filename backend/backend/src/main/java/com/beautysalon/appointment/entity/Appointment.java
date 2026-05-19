package com.beautysalon.appointment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * OOP Concepts Applied:
 * - Encapsulation: Private fields with public getters and setters to protect state.
 * - Inheritance: Subclasses inheriting from Abstract classes (if applicable) for code reuse.
 * - Abstraction: Defining core properties without exposing complex implementation logic.
 * - Polymorphism: Allowing entities to be treated as their abstract types.
 */
public class Appointment {
    private String id;
    private String customerId;
    private String staffId;
    private String serviceId;
    private String date; // YYYY-MM-DD
    private String time; // HH:MM
    private int durationMinutes;
    private String status; // BOOKED, CANCELLED, COMPLETED
}
