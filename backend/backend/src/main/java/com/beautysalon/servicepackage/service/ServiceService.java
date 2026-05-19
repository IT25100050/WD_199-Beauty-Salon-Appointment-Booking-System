package com.beautysalon.servicepackage.service;

import com.beautysalon.servicepackage.dto.ServiceDto;
import com.beautysalon.servicepackage.entity.PremiumService;
import com.beautysalon.servicepackage.entity.RegularService;
import com.beautysalon.servicepackage.entity.Service;
import com.beautysalon.servicepackage.exception.ServiceNotFoundException;
import com.beautysalon.servicepackage.repository.ServiceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ServiceDto getServiceById(String id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + id));
        return convertToDto(service);
    }

    public ServiceDto createService(ServiceDto dto) {
        String id = UUID.randomUUID().toString();
        Service service;
        if ("PREMIUM".equalsIgnoreCase(dto.getType())) {
            service = new PremiumService(id, dto.getName(), dto.getCategory(), dto.getPrice(),
                    dto.getDurationMinutes(), dto.isAvailable(), dto.getPremiumAmenities(), dto.getLuxurySurcharge());
        } else {
            service = new RegularService(id, dto.getName(), dto.getCategory(), dto.getPrice(),
                    dto.getDurationMinutes(), dto.isAvailable(), dto.getStandardProductsUsed());
        }
        serviceRepository.save(service);
        return convertToDto(service);
    }

    public ServiceDto updateService(String id, ServiceDto dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + id));

        service.setName(dto.getName());
        service.setCategory(dto.getCategory());
        service.setPrice(dto.getPrice());
        service.setDurationMinutes(dto.getDurationMinutes());
        service.setAvailable(dto.isAvailable());

        if (service instanceof PremiumService premium) {
            premium.setPremiumAmenities(dto.getPremiumAmenities());
            premium.setLuxurySurcharge(dto.getLuxurySurcharge());
        } else if (service instanceof RegularService regular) {
            regular.setStandardProductsUsed(dto.getStandardProductsUsed());
        }

        serviceRepository.update(service);
        return convertToDto(service);
    }

    public void deleteService(String id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with ID: " + id));
        serviceRepository.deleteById(id);
    }

    private ServiceDto convertToDto(Service service) {
        ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setCategory(service.getCategory());
        dto.setPrice(service.getPrice());
        dto.setDurationMinutes(service.getDurationMinutes());
        dto.setAvailable(service.isAvailable());

        if (service instanceof PremiumService premium) {
            dto.setType("PREMIUM");
            dto.setPremiumAmenities(premium.getPremiumAmenities());
            dto.setLuxurySurcharge(premium.getLuxurySurcharge());
        } else if (service instanceof RegularService regular) {
            dto.setType("REGULAR");
            dto.setStandardProductsUsed(regular.getStandardProductsUsed());
        }
        return dto;
    }
}
