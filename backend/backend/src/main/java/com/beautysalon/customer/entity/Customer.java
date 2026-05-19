package com.beautysalon.customer.entity;

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
public class Customer extends User {
    private String phoneNumber;
    private String address;

    public Customer(String id, String username, String password, String email, String role, String phoneNumber, String address) {
        super(id, username, password, email, role);
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
