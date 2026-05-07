package com.beauty_salon.dto;

import com.beauty_salon.model.FeedbackStatus;
import com.beauty_salon.model.ReviewType;
import java.time.LocalDateTime;

public class FeedbackDTO {

    private Long id;

    // Customer info
    private Long customerId;
    private String customerName;    // firstName + lastName (auto-filled)

    // Service info
    private Long serviceId;
    private String serviceName;     // auto-filled from service

    // Review details
    private Integer rating;         // 1 to 5 stars
    private String comment;
    private String adminReply;

    // Status & type
    private FeedbackStatus status;
    private ReviewType reviewType;

    // Timestamps
    private LocalDateTime submittedAt;
    private LocalDateTime repliedAt;

    // ── Getters & Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }

    public FeedbackStatus getStatus() { return status; }
    public void setStatus(FeedbackStatus status) { this.status = status; }

    public ReviewType getReviewType() { return reviewType; }
    public void setReviewType(ReviewType reviewType) { this.reviewType = reviewType; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getRepliedAt() { return repliedAt; }
    public void setRepliedAt(LocalDateTime repliedAt) { this.repliedAt = repliedAt; }
}
