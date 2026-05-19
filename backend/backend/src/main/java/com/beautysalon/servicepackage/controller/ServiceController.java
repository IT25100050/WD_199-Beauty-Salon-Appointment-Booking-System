package com.beautysalon.servicepackage.controller;

import com.beautysalon.servicepackage.dto.ServiceDto;
import com.beautysalon.servicepackage.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
/**
 * OOP Concepts Applied:
 * - Encapsulation: Exposes REST endpoints while hiding internal business logic.
 * - Abstraction: Interacts with Service interfaces/classes without knowing their implementation details.
 * - Single Responsibility Principle (SOLID): Responsible solely for handling HTTP requests and responses.
 */
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getServiceById(@PathVariable String id) {
        return ResponseEntity.ok(serviceService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceDto> createService(@RequestBody ServiceDto dto) {
        return ResponseEntity.ok(serviceService.createService(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable String id, @RequestBody ServiceDto dto) {
        return ResponseEntity.ok(serviceService.updateService(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable String id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
