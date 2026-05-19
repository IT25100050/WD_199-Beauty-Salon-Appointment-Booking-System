package com.beautysalon.auth.repository;

import com.beautysalon.auth.entity.User;
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
public class UserRepository extends AbstractFileRepository<User, String> {

    @Override
    protected String getFilePath() {
        return "users.txt";
    }

    @Override
    protected String serialize(User user) {
        return String.join("|",
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Override
    protected User deserialize(String record) {
        String[] parts = record.split("\\|");
        User user = new User();
        user.setId(parts[0]);
        user.setUsername(parts[1]);
        user.setPassword(parts[2]);
        user.setEmail(parts[3]);
        user.setRole(parts[4]);
        return user;
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(User user) {
        return user.getId() + "|";
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
