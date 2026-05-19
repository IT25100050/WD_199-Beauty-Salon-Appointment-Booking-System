package com.beautysalon.inventory.entity;

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
public class CosmeticProduct extends Product {
    private String expiryDate;
    private boolean isOrganic;

    public CosmeticProduct(String id, String name, String category, int quantity, double price, String supplier, String imageUrl, String expiryDate, boolean isOrganic) {
        super(id, name, category, quantity, price, supplier, imageUrl);
        this.expiryDate = expiryDate;
        this.isOrganic = isOrganic;
    }
}
