package com.beautysalon.staff.repository;

import com.beautysalon.staff.entity.Staff;
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
public class StaffRepository extends AbstractFileRepository<Staff, String> {

    @Override
    protected String getFilePath() {
        return "staff.txt";
    }

    @Override
    protected String serialize(Staff staff) {
        return String.join("|",
                staff.getId(),
                staff.getUsername(),
                staff.getPassword(),
                staff.getEmail(),
                staff.getRole(),
                staff.getSpecialization(),
                staff.getSchedule(),
                String.valueOf(staff.isAvailable())
        );
    }

    @Override
    protected Staff deserialize(String record) {
        String[] parts = record.split("\\|");
        return new Staff(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5],
                parts[6],
                Boolean.parseBoolean(parts[7])
        );
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Staff staff) {
        return staff.getId() + "|";
    }

    public Optional<Staff> findByUsername(String username) {
        return findAll().stream()
                .filter(s -> s.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
}
