package com.beautysalon.appointment.dto;

import lombok.Data;

@Data
public class AppointmentDto {
    private String id;
    private String customerId;
    private String staffId;
    private String serviceId;
    private String date; // YYYY-MM-DD
    private String time; // HH:MM
    private int durationMinutes;
    private String status; // BOOKED, CANCELLED, COMPLETED
}
