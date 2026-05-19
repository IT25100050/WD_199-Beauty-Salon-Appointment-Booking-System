package com.beautysalon.customer.repository;

import com.beautysalon.customer.entity.Customer;
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
public class CustomerRepository extends AbstractFileRepository<Customer, String> {

    @Override
    protected String getFilePath() {
        return "users.txt";
    }

    @Override
    protected String serialize(Customer customer) {
        return String.join("|",
                customer.getId(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getEmail(),
                customer.getRole(),
                customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "",
                customer.getAddress() != null ? customer.getAddress() : ""
        );
    }

    @Override
    protected Customer deserialize(String record) {
        String[] parts = record.split("\\|");
        Customer customer = new Customer();
        customer.setId(parts[0]);
        customer.setUsername(parts[1]);
        customer.setPassword(parts[2]);
        customer.setEmail(parts[3]);
        customer.setRole(parts[4]);
        if (parts.length > 5) {
            customer.setPhoneNumber(parts[5]);
        }
        if (parts.length > 6) {
            customer.setAddress(parts[6]);
        }
        return customer;
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Customer customer) {
        return customer.getId() + "|";
    }

    public Optional<Customer> findByUsername(String username) {
        return findAll().stream()
                .filter(c -> c.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }
}
