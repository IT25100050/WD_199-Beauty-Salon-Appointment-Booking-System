package com.beautysalon.servicepackage.entity;

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
public class PremiumService extends Service {
    private String premiumAmenities;
    private double luxurySurcharge;

    public PremiumService(String id, String name, String category, double price, int durationMinutes, boolean isAvailable, String premiumAmenities, double luxurySurcharge) {
        super(id, name, category, price, durationMinutes, isAvailable);
        this.premiumAmenities = premiumAmenities;
        this.luxurySurcharge = luxurySurcharge;
    }
}
