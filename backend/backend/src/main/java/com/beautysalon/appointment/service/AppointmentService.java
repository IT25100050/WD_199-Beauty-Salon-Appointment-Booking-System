package com.beautysalon.appointment.service;

import com.beautysalon.appointment.dto.AppointmentDto;
import com.beautysalon.appointment.entity.Appointment;
import com.beautysalon.appointment.exception.AppointmentConflictException;
import com.beautysalon.appointment.repository.AppointmentRepository;
import com.beautysalon.customer.repository.CustomerRepository;
import com.beautysalon.servicepackage.entity.Service;
import com.beautysalon.servicepackage.exception.ServiceNotFoundException;
import com.beautysalon.servicepackage.repository.ServiceRepository;
import com.beautysalon.staff.entity.Staff;
import com.beautysalon.staff.exception.UserNotFoundException;
import com.beautysalon.staff.repository.StaffRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              StaffRepository staffRepository,
                              CustomerRepository customerRepository,
                              ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.staffRepository = staffRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public AppointmentDto getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        return convertToDto(appointment);
    }

    public AppointmentDto bookAppointment(AppointmentDto dto) {
        customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new UserNotFoundException("Customer not found with ID: " + dto.getCustomerId()));

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new UserNotFoundException("Staff not found with ID: " + dto.getStaffId()));
        if (!staff.isAvailable()) {
            throw new AppointmentConflictException("Selected staff member is currently not available");
        }

        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + dto.getServiceId()));

        int duration = dto.getDurationMinutes() > 0 ? dto.getDurationMinutes() : service.getDurationMinutes();

        validateNoOverlap(dto.getStaffId(), dto.getDate(), dto.getTime(), duration, null);

        String id = UUID.randomUUID().toString();
        Appointment appointment = new Appointment(id, dto.getCustomerId(), dto.getStaffId(), dto.getServiceId(),
                dto.getDate(), dto.getTime(), duration, "BOOKED");

        appointmentRepository.save(appointment);
        return convertToDto(appointment);
    }

    public AppointmentDto rescheduleAppointment(String id, String date, String time) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

        validateNoOverlap(appointment.getStaffId(), date, time, appointment.getDurationMinutes(), id);

        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus("BOOKED");
        appointmentRepository.update(appointment);

        return convertToDto(appointment);
    }

    public AppointmentDto cancelAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

        appointment.setStatus("CANCELLED");
        appointmentRepository.update(appointment);
        return convertToDto(appointment);
    }

    public void deleteAppointment(String id) {
    Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

    appointmentRepository.deleteById(appointment.getId());
}

    public List<AppointmentDto> getAppointmentHistory(String customerId) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getCustomerId().equals(customerId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void validateNoOverlap(String staffId, String date, String time, int durationMinutes, String excludeId) {
        int newStart = parseTimeToMinutes(time);
        int newEnd = newStart + durationMinutes;

        List<Appointment> existing = appointmentRepository.findAll().stream()
                .filter(a -> a.getStaffId().equals(staffId) && a.getDate().equals(date) && !"CANCELLED".equals(a.getStatus()))
                .filter(a -> excludeId == null || !a.getId().equals(excludeId))
                .toList();

        for (Appointment app : existing) {
            int extStart = parseTimeToMinutes(app.getTime());
            int extEnd = extStart + app.getDurationMinutes();

            if (newStart < extEnd && extStart < newEnd) {
                throw new AppointmentConflictException("Time conflict! The staff member is already booked from " 
                        + app.getTime() + " for " + app.getDurationMinutes() + " mins.");
            }
        }
    }

    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private AppointmentDto convertToDto(Appointment a) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(a.getId());
        dto.setCustomerId(a.getCustomerId());
        dto.setStaffId(a.getStaffId());
        dto.setServiceId(a.getServiceId());
        dto.setDate(a.getDate());
        dto.setTime(a.getTime());
        dto.setDurationMinutes(a.getDurationMinutes());
        dto.setStatus(a.getStatus());
        return dto;
    }
}
