package com.beautysalon.admin.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    private String generatedAt;
    private double totalRevenue;
    private Map<String, Double> revenueByPaymentMethod;
    private Map<String, Long> bookingsByService;
    private List<String> staffPerformance;
    private List<String> productUsageReport;
}
