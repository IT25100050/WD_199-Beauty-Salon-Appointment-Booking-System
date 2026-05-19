package com.beautysalon.customer.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
}
