package com.beautysalon.staff.entity;

import com.beautysalon.auth.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
/**
 * OOP Concepts Applied:
 * - Encapsulation: Private fields with public getters and setters to protect state.
 * - Inheritance: Subclasses inheriting from Abstract classes (if applicable) for code reuse.
 * - Abstraction: Defining core properties without exposing complex implementation logic.
 * - Polymorphism: Allowing entities to be treated as their abstract types.
 */
public class Staff extends User {
    private String specialization; // e.g. Hair Stylist, Nail Artist, Bridal Specialist, Spa Therapist
    private String schedule; // e.g. "Monday-Friday 9AM-5PM"
    private boolean isAvailable;

    public Staff(String id, String username, String password, String email, String role, String specialization, String schedule, boolean isAvailable) {
        super(id, username, password, email, role);
        this.specialization = specialization;
        this.schedule = schedule;
        this.isAvailable = isAvailable;
    }
}
