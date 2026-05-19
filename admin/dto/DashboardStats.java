package com.beautysalon.admin.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStats {
    private long totalCustomers;
    private long totalStaff;
    private long totalAppointments;
    private double totalRevenue;
    private long lowStockCount;
    private long pendingReviewsCount;
    private Map<String, Long> appointmentsByStatus;
    private List<String> lowStockAlerts;
}
