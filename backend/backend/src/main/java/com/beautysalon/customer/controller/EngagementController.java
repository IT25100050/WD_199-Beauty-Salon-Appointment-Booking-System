package com.beautysalon.customer.controller;

import com.beautysalon.customer.dto.ContactRequest;
import com.beautysalon.customer.dto.NewsletterRequest;
import com.beautysalon.customer.service.EngagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/engagement")
/**
 * OOP Concepts Applied:
 * - Encapsulation: Exposes REST endpoints while hiding internal business logic.
 * - Abstraction: Interacts with Service interfaces/classes without knowing their implementation details.
 * - Single Responsibility Principle (SOLID): Responsible solely for handling HTTP requests and responses.
 */
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> contact(@RequestBody ContactRequest request) {
        return ResponseEntity.ok(engagementService.saveContact(request));
    }

    @PostMapping("/newsletter")
    public ResponseEntity<String> newsletter(@RequestBody NewsletterRequest request) {
        return ResponseEntity.ok(engagementService.saveNewsletter(request));
    }
}
