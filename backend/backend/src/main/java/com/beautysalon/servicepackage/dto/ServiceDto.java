package com.beautysalon.servicepackage.dto;

import lombok.Data;

@Data
public class ServiceDto {
    private String id;
    private String name;
    private String category;
    private double price;
    private int durationMinutes;
    private boolean isAvailable;
    private String type; // REGULAR or PREMIUM
    private String standardProductsUsed;
    private String premiumAmenities;
    private double luxurySurcharge;
}
