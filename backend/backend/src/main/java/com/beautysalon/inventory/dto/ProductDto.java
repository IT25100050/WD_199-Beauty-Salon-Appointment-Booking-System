package com.beautysalon.inventory.dto;

import lombok.Data;

@Data
public class ProductDto {
    private String id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String supplier;
    private String type; // COSMETIC or EQUIPMENT
    private String imageUrl;
    
    // Cosmetic specific
    private String expiryDate;
    private boolean isOrganic;

    // Equipment specific
    private int warrantyMonths;
    private String lastServicedDate;
}
