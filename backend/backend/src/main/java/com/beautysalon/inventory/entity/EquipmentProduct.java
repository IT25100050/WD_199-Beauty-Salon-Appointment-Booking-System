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
public class EquipmentProduct extends Product {
    private int warrantyMonths;
    private String lastServicedDate;

    public EquipmentProduct(String id, String name, String category, int quantity, double price, String supplier, String imageUrl, int warrantyMonths, String lastServicedDate) {
        super(id, name, category, quantity, price, supplier, imageUrl);
        this.warrantyMonths = warrantyMonths;
        this.lastServicedDate = lastServicedDate;
    }
}
