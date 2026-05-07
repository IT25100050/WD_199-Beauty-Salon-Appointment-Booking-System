package com.beauty_salon.repository;

import com.beauty_salon.model.Feedback;
import com.beauty_salon.model.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Get all feedback by status (PENDING / APPROVED / REJECTED)
    List<Feedback> findByStatus(FeedbackStatus status);

    // Get all feedback for a specific customer
    List<Feedback> findByCustomerId(Long customerId);

    // Get all feedback for a specific service
    List<Feedback> findByServiceId(Long serviceId);

    // Get all approved feedback for a service (public view)
    List<Feedback> findByServiceIdAndStatus(Long serviceId, FeedbackStatus status);

    // Get all feedback ordered by newest first
    List<Feedback> findAllByOrderBySubmittedAtDesc();

    // Get all approved feedback ordered by newest first
    List<Feedback> findByStatusOrderBySubmittedAtDesc(FeedbackStatus status);

    // Get all feedback by a specific star rating
    List<Feedback> findByRating(Integer rating);

    // Get average rating for a specific service
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.service.id = :serviceId AND f.status = 'APPROVED'")
    Double findAverageRatingByServiceId(Long serviceId);

    // Get overall average rating across all approved reviews
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.status = 'APPROVED'")
    Double findOverallAverageRating();

    // Count feedback by status
    long countByStatus(FeedbackStatus status);
}
