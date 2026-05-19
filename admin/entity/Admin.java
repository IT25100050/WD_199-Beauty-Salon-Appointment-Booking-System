package com.beautysalon.admin.entity;

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
public class Admin extends User {
    private boolean isSuperAdmin;

    public Admin(String id, String username, String password, String email, String role, boolean isSuperAdmin) {
        super(id, username, password, email, role);
        this.isSuperAdmin = isSuperAdmin;
    }
}
