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
public class Service extends AbstractService {
    public Service(String id, String name, String category, double price, int durationMinutes, boolean isAvailable) {
        super(id, name, category, price, durationMinutes, isAvailable);
    }
}
