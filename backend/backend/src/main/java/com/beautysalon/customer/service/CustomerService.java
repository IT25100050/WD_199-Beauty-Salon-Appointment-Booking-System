package com.beautysalon.customer.service;

import com.beautysalon.customer.dto.CustomerDto;
import com.beautysalon.customer.entity.Customer;
import com.beautysalon.customer.exception.UserNotFoundException;
import com.beautysalon.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + id));
        return convertToDto(customer);
    }

    public CustomerDto updateCustomer(String id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + id));
        
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddress(dto.getAddress());
        
        customerRepository.update(customer);
        return convertToDto(customer);
    }

    public void deleteCustomer(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + id));
        customerRepository.deleteById(id);
    }

    public List<CustomerDto> searchCustomers(String query) {
        return customerRepository.findAll().stream()
                .filter(c -> c.getUsername().toLowerCase().contains(query.toLowerCase()) || 
                             c.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                             (c.getPhoneNumber() != null && c.getPhoneNumber().contains(query)))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CustomerDto convertToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        return dto;
    }
}
