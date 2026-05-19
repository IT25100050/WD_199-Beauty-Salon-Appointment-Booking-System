package com.beautysalon.admin.repository;

import com.beautysalon.admin.entity.Admin;
import com.beautysalon.util.AbstractFileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/**
 * OOP Concepts Applied:
 * - Abstraction: Provides an interface or abstract mechanism for data access without exposing persistence details.
 * - Inheritance: Inherits common CRUD operations from abstract repository classes.
 * - Encapsulation: Hides file/database reading and writing mechanics from the rest of the application.
 */
public class AdminRepository extends AbstractFileRepository<Admin, String> {

    @Override
    protected String getFilePath() {
        return "admins.txt";
    }

    @Override
    protected String serialize(Admin admin) {
        return String.join("|",
                admin.getId(),
                admin.getUsername(),
                admin.getPassword(),
                admin.getEmail(),
                admin.getRole(),
                String.valueOf(admin.isSuperAdmin())
        );
    }

    @Override
    protected Admin deserialize(String record) {
        String[] parts = record.split("\\|");
        return new Admin(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                Boolean.parseBoolean(parts[5])
        );
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Admin admin) {
        return admin.getId() + "|";
    }

    public Optional<Admin> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
}
