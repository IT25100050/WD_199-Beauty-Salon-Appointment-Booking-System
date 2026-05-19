package com.beautysalon.staff.dto;

import lombok.Data;

@Data
public class StaffDto {
    private String id;
    private String username;
    private String email;
    private String specialization;
    private String schedule;
    private boolean isAvailable;
}
