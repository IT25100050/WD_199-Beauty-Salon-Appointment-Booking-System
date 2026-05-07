package com.beauty_salon.service;

import com.beauty_salon.dto.FeedbackDTO;
import com.beauty_salon.model.*;
import com.beauty_salon.repository.CustomerRepository;
import com.beauty_salon.repository.FeedbackRepository;
import com.beauty_salon.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;
    private final ServiceRepository serviceRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           CustomerRepository customerRepository,
                           ServiceRepository serviceRepository) {
        this.feedbackRepository = feedbackRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
    }

    // ── Get all feedback (Admin view) ──
    public List<FeedbackDTO> getAllFeedback() {
        return feedbackRepository.findAllByOrderBySubmittedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get only approved feedback (Public view) ──
    public List<FeedbackDTO> getApprovedFeedback() {
        return feedbackRepository
                .findByStatusOrderBySubmittedAtDesc(FeedbackStatus.APPROVED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get all pending feedback (Admin moderation) ──
    public List<FeedbackDTO> getPendingFeedback() {
        return feedbackRepository.findByStatus(FeedbackStatus.PENDING)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get feedback by ID ──
    public FeedbackDTO getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        return toDTO(feedback);
    }

    // ── Get all feedback for a customer ──
    public List<FeedbackDTO> getFeedbackByCustomer(Long customerId) {
        return feedbackRepository.findByCustomerId(customerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get approved feedback for a service ──
    public List<FeedbackDTO> getApprovedFeedbackByService(Long serviceId) {
        return feedbackRepository
                .findByServiceIdAndStatus(serviceId, FeedbackStatus.APPROVED)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Submit new feedback ──
    public FeedbackDTO submitFeedback(FeedbackDTO dto) {

        // Validate rating
        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        // Validate customer exists
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + dto.getCustomerId()));

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        feedback.setStatus(FeedbackStatus.PENDING); // always starts as PENDING
        feedback.setReviewType(dto.getReviewType() != null ? dto.getReviewType() : ReviewType.GUEST);

        // Link service if provided
        if (dto.getServiceId() != null) {
            serviceRepository.findById(dto.getServiceId())
                    .ifPresent(feedback::setService);
        }

        return toDTO(feedbackRepository.save(feedback));
    }

    // ── Edit feedback (customer edits their own review) ──
    public FeedbackDTO updateFeedback(Long id, FeedbackDTO dto) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        if (dto.getRating() != null) {
            if (dto.getRating() < 1 || dto.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5.");
            }
            feedback.setRating(dto.getRating());
        }

        if (dto.getComment() != null) feedback.setComment(dto.getComment());

        // Reset to PENDING when customer edits review
        feedback.setStatus(FeedbackStatus.PENDING);

        return toDTO(feedbackRepository.save(feedback));
    }

    // ── Approve feedback (Admin) ──
    public FeedbackDTO approveFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        feedback.setStatus(FeedbackStatus.APPROVED);
        return toDTO(feedbackRepository.save(feedback));
    }

    // ── Reject feedback (Admin) ──
    public FeedbackDTO rejectFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        feedback.setStatus(FeedbackStatus.REJECTED);
        return toDTO(feedbackRepository.save(feedback));
    }

    // ── Admin reply to a review ──
    public FeedbackDTO replyToFeedback(Long id, String replyText) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        if (replyText == null || replyText.trim().isEmpty()) {
            throw new RuntimeException("Reply text cannot be empty.");
        }

        feedback.setAdminReply(replyText);
        feedback.setRepliedAt(LocalDateTime.now());
        feedback.setStatus(FeedbackStatus.APPROVED); // auto-approve when admin replies

        return toDTO(feedbackRepository.save(feedback));
    }

    // ── Delete feedback ──
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found with id: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    // ── Get average rating for a service ──
    public Double getAverageRatingByService(Long serviceId) {
        Double avg = feedbackRepository.findAverageRatingByServiceId(serviceId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // ── Get overall average rating ──
    public Double getOverallAverageRating() {
        Double avg = feedbackRepository.findOverallAverageRating();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    // ── Get feedback statistics (for dashboard) ──
    public FeedbackStatsDTO getStats() {
        FeedbackStatsDTO stats = new FeedbackStatsDTO();
        stats.setTotalFeedback(feedbackRepository.count());
        stats.setPendingCount(feedbackRepository.countByStatus(FeedbackStatus.PENDING));
        stats.setApprovedCount(feedbackRepository.countByStatus(FeedbackStatus.APPROVED));
        stats.setRejectedCount(feedbackRepository.countByStatus(FeedbackStatus.REJECTED));
        stats.setOverallAverageRating(getOverallAverageRating());
        return stats;
    }

    // ── Map Entity → DTO ──
    public FeedbackDTO toDTO(Feedback f) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(f.getId());
        dto.setRating(f.getRating());
        dto.setComment(f.getComment());
        dto.setAdminReply(f.getAdminReply());
        dto.setStatus(f.getStatus());
        dto.setReviewType(f.getReviewType());
        dto.setSubmittedAt(f.getSubmittedAt());
        dto.setRepliedAt(f.getRepliedAt());

        if (f.getCustomer() != null) {
            dto.setCustomerId(f.getCustomer().getId());
            dto.setCustomerName(f.getCustomer().getFirstName() + " " + f.getCustomer().getLastName());
        }
        if (f.getService() != null) {
            dto.setServiceId(f.getService().getId());
            dto.setServiceName(f.getService().getName());
        }
        return dto;
    }

    // ── Inner stats DTO ──
    public static class FeedbackStatsDTO {
        private long totalFeedback;
        private long pendingCount;
        private long approvedCount;
        private long rejectedCount;
        private double overallAverageRating;

        public long getTotalFeedback() { return totalFeedback; }
        public void setTotalFeedback(long totalFeedback) { this.totalFeedback = totalFeedback; }

        public long getPendingCount() { return pendingCount; }
        public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

        public long getApprovedCount() { return approvedCount; }
        public void setApprovedCount(long approvedCount) { this.approvedCount = approvedCount; }

        public long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(long rejectedCount) { this.rejectedCount = rejectedCount; }

        public double getOverallAverageRating() { return overallAverageRating; }
        public void setOverallAverageRating(double overallAverageRating) { this.overallAverageRating = overallAverageRating; }
    }
}
