package com.beautysalon.admin.service;

import com.beautysalon.admin.dto.DashboardStats;
import com.beautysalon.admin.dto.ReportData;
import com.beautysalon.appointment.entity.Appointment;
import com.beautysalon.appointment.repository.AppointmentRepository;
import com.beautysalon.customer.repository.CustomerRepository;
import com.beautysalon.inventory.entity.Product;
import com.beautysalon.inventory.repository.ProductRepository;
import com.beautysalon.payment.entity.Payment;
import com.beautysalon.payment.repository.PaymentRepository;
import com.beautysalon.review.entity.Review;
import com.beautysalon.review.repository.ReviewRepository;
import com.beautysalon.staff.entity.Staff;
import com.beautysalon.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public AdminDashboardService(CustomerRepository customerRepository,
                                 StaffRepository staffRepository,
                                 AppointmentRepository appointmentRepository,
                                 PaymentRepository paymentRepository,
                                 ProductRepository productRepository,
                                 ReviewRepository reviewRepository) {
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        stats.setTotalCustomers(customerRepository.findAll().size());
        stats.setTotalStaff(staffRepository.findAll().size());

        List<Appointment> appointments = appointmentRepository.findAll();
        stats.setTotalAppointments(appointments.size());

        double revenue = paymentRepository.findAll().stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        stats.setTotalRevenue(revenue);

        List<Product> products = productRepository.findAll();
        long lowStockCount = products.stream().filter(p -> p.getQuantity() < 5).count();
        stats.setLowStockCount(lowStockCount);

        List<String> lowStockAlerts = products.stream()
                .filter(p -> p.getQuantity() < 5)
                .map(p -> p.getName() + " (Stock: " + p.getQuantity() + ")")
                .collect(Collectors.toList());
        stats.setLowStockAlerts(lowStockAlerts);

        List<Review> reviews = reviewRepository.findAll();
        long pendingReviews = reviews.stream().filter(r -> !r.isApproved()).count();
        stats.setPendingReviewsCount(pendingReviews);

        Map<String, Long> appointmentsByStatus = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        stats.setAppointmentsByStatus(appointmentsByStatus);

        return stats;
    }

    public ReportData generateReports() {
        ReportData report = new ReportData();
        report.setGeneratedAt(LocalDateTime.now().toString());

        List<Payment> payments = paymentRepository.findAll();
        double revenue = payments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        report.setTotalRevenue(revenue);

        Map<String, Double> revenueByPaymentMethod = payments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.groupingBy(Payment::getPaymentMethod, Collectors.summingDouble(Payment::getAmount)));
        report.setRevenueByPaymentMethod(revenueByPaymentMethod);

        List<Appointment> appointments = appointmentRepository.findAll();
        Map<String, Long> bookingsByService = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getServiceId, Collectors.counting()));
        report.setBookingsByService(bookingsByService);

        List<Staff> staffList = staffRepository.findAll();
        List<String> staffPerformance = new ArrayList<>();
        for (Staff staff : staffList) {
            long completedBookings = appointments.stream()
                    .filter(a -> a.getStaffId().equals(staff.getId()) && "COMPLETED".equals(a.getStatus()))
                    .count();
            staffPerformance.add(staff.getUsername() + " - Completed Appointments: " + completedBookings);
        }
        report.setStaffPerformance(staffPerformance);

        List<Product> products = productRepository.findAll();
        List<String> productUsageReport = products.stream()
                .map(p -> p.getName() + " - Stock Left: " + p.getQuantity() + " (Supplier: " + p.getSupplier() + ")")
                .collect(Collectors.toList());
        report.setProductUsageReport(productUsageReport);

        return report;
    }
}
