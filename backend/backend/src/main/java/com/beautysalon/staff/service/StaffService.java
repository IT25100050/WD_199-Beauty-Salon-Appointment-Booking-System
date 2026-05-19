package com.beautysalon.staff.service;

import com.beautysalon.staff.dto.StaffDto;
import com.beautysalon.staff.entity.Staff;
import com.beautysalon.staff.exception.UserNotFoundException;
import com.beautysalon.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public StaffDto getStaffById(String id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Staff not found with ID: " + id));
        return convertToDto(staff);
    }

    public StaffDto createStaff(StaffDto dto) {
        String id = UUID.randomUUID().toString();
        Staff staff = new Staff(id, dto.getUsername(), "defaultPass123", dto.getEmail(), "STAFF",
                dto.getSpecialization(), dto.getSchedule(), dto.isAvailable());
        staffRepository.save(staff);
        return convertToDto(staff);
    }

    public StaffDto updateStaff(String id, StaffDto dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Staff not found with ID: " + id));

        staff.setEmail(dto.getEmail());
        staff.setSpecialization(dto.getSpecialization());
        staff.setSchedule(dto.getSchedule());
        staff.setAvailable(dto.isAvailable());

        staffRepository.update(staff);
        return convertToDto(staff);
    }

    public void deleteStaff(String id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Staff not found with ID: " + id));
        staffRepository.deleteById(id);
    }

    public List<StaffDto> searchStaff(String query) {
        return staffRepository.findAll().stream()
                .filter(s -> s.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                             s.getSpecialization().toLowerCase().contains(query.toLowerCase()) ||
                             s.getEmail().toLowerCase().contains(query.toLowerCase()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private StaffDto convertToDto(Staff staff) {
        StaffDto dto = new StaffDto();
        dto.setId(staff.getId());
        dto.setUsername(staff.getUsername());
        dto.setEmail(staff.getEmail());
        dto.setSpecialization(staff.getSpecialization());
        dto.setSchedule(staff.getSchedule());
        dto.setAvailable(staff.isAvailable());
        return dto;
    }
}
