package com.beauty_salon.controller;

import com.beauty_salon.dto.FeedbackDTO;
import com.beauty_salon.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ══════════════════════════════════════════
    //           PUBLIC ENDPOINTS
    //   (customers can access these)
    // ══════════════════════════════════════════

    // GET /api/feedback/approved
    // Get all approved reviews — shown on public page
    @GetMapping("/approved")
    public ResponseEntity<List<FeedbackDTO>> getApprovedFeedback() {
        return ResponseEntity.ok(feedbackService.getApprovedFeedback());
    }

    // GET /api/feedback/service/{serviceId}
    // Get all approved reviews for a specific service
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<FeedbackDTO>> getByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(feedbackService.getApprovedFeedbackByService(serviceId));
    }

    // GET /api/feedback/service/{serviceId}/rating
    // Get average star rating for a service
    @GetMapping("/service/{serviceId}/rating")
    public ResponseEntity<Map<String, Object>> getServiceRating(@PathVariable Long serviceId) {
        Double avg = feedbackService.getAverageRatingByService(serviceId);
        return ResponseEntity.ok(Map.of(
                "serviceId", serviceId,
                "averageRating", avg
        ));
    }

    // GET /api/feedback/rating/overall
    // Get overall salon rating
    @GetMapping("/rating/overall")
    public ResponseEntity<Map<String, Object>> getOverallRating() {
        return ResponseEntity.ok(Map.of(
                "overallAverageRating", feedbackService.getOverallAverageRating()
        ));
    }

    // POST /api/feedback
    // Customer submits a new review
    @PostMapping
    public ResponseEntity<FeedbackDTO> submitFeedback(@RequestBody FeedbackDTO dto) {
        FeedbackDTO created = feedbackService.submitFeedback(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/feedback/{id}
    // Customer edits their own review
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(@PathVariable Long id,
                                                       @RequestBody FeedbackDTO dto) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, dto));
    }

    // GET /api/feedback/customer/{customerId}
    // Customer views their own submitted reviews
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<FeedbackDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByCustomer(customerId));
    }

    // ══════════════════════════════════════════
    //           ADMIN ENDPOINTS
    //   (only admin/staff can access these)
    // ══════════════════════════════════════════

    // GET /api/feedback
    // Admin views ALL feedback (pending, approved, rejected)
    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    // GET /api/feedback/{id}
    // Get a specific feedback by ID
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    // GET /api/feedback/pending
    // Admin views all pending reviews waiting for moderation
    @GetMapping("/pending")
    public ResponseEntity<List<FeedbackDTO>> getPendingFeedback() {
        return ResponseEntity.ok(feedbackService.getPendingFeedback());
    }

    // GET /api/feedback/stats
    // Admin dashboard stats — total, pending, approved, rejected, avg rating
    @GetMapping("/stats")
    public ResponseEntity<FeedbackService.FeedbackStatsDTO> getStats() {
        return ResponseEntity.ok(feedbackService.getStats());
    }

    // PUT /api/feedback/{id}/approve
    // Admin approves a review — makes it visible to public
    @PutMapping("/{id}/approve")
    public ResponseEntity<FeedbackDTO> approveFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.approveFeedback(id));
    }

    // PUT /api/feedback/{id}/reject
    // Admin rejects a review — hides it from public
    @PutMapping("/{id}/reject")
    public ResponseEntity<FeedbackDTO> rejectFeedback(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.rejectFeedback(id));
    }

    // PUT /api/feedback/{id}/reply
    // Admin replies to a review
    @PutMapping("/{id}/reply")
    public ResponseEntity<FeedbackDTO> replyToFeedback(@PathVariable Long id,
                                                        @RequestBody Map<String, String> body) {
        String replyText = body.get("reply");
        return ResponseEntity.ok(feedbackService.replyToFeedback(id, replyText));
    }

    // DELETE /api/feedback/{id}
    // Admin or customer deletes a review
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
