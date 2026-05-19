package com.beautysalon.servicepackage.entity;

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
public abstract class AbstractService {
    private String id;
    private String name;
    private String category;
    private double price;
    private int durationMinutes;
    private boolean isAvailable;
}
