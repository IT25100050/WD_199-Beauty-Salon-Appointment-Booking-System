package com.beautysalon.appointment.controller;

import com.beautysalon.appointment.dto.AppointmentDto;
import com.beautysalon.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
/**
 * OOP Concepts Applied:
 * - Encapsulation: Exposes REST endpoints while hiding internal business logic.
 * - Abstraction: Interacts with Service interfaces/classes without knowing their implementation details.
 * - Single Responsibility Principle (SOLID): Responsible solely for handling HTTP requests and responses.
 */
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> bookAppointment(@RequestBody AppointmentDto dto) {
        return ResponseEntity.ok(appointmentService.bookAppointment(dto));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentDto> rescheduleAppointment(@PathVariable String id,
                                                                 @RequestParam String date,
                                                                 @RequestParam String time) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, date, time));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<AppointmentDto>> getAppointmentHistory(@PathVariable String customerId) {
        return ResponseEntity.ok(appointmentService.getAppointmentHistory(customerId));
    }
}
