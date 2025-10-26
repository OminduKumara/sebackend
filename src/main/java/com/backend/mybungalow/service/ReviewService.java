package com.backend.mybungalow.service;

import com.backend.mybungalow.model.Review;
import com.backend.mybungalow.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class ReviewService {
    private final ReviewRepository repo;

    public ReviewService(ReviewRepository repo) {
        this.repo = repo;
    }

    public Review create(String name, String email, String message) {
        var review = Review.builder()
                .customerName(name)
                .customerEmail(email)
                .message(message)
                .build();
        return repo.save(review);
    }

    // Public view (only name + message + createdAt + reply)
    public List<Review> listPublic() {
        return repo.findAll();
    }

    // Admin view (full details)
    public List<Review> listAdmin() {
        return repo.findAll();
    }

    public Review reply(Long id, String reply) {
        var review = repo.findById(id).orElseThrow();
        review.setAdminReply(reply);
        review.setRepliedAt(Instant.now());
        return repo.save(review);
    }

    public void clearReply(Long id) {
        var review = repo.findById(id).orElseThrow();
        review.setAdminReply(null);
        review.setRepliedAt(null);
        repo.save(review);
    }

    public List<Review> listByCustomerEmail(String email) {
        if (email == null || email.isBlank()) return List.of();
        return repo.findByCustomerEmailOrderByCreatedAtDesc(email);
    }

    public Review updateMessage(Long id, String newMessage, String requesterEmail, boolean isAdmin) {
        var review = repo.findById(id).orElseThrow();
        if (!isAdmin) {
            // Only the owner (by email) can edit when not admin
            if (requesterEmail == null || !Objects.equals(review.getCustomerEmail(), requesterEmail)) {
                throw new SecurityException("Not authorized to edit this review");
            }
        }
        review.setMessage(newMessage);
        return repo.save(review);
    }

    public void deleteReview(Long id, String requesterEmail, boolean isAdmin) {
        var review = repo.findById(id).orElseThrow();
        if (!isAdmin) {
            if (requesterEmail == null || !Objects.equals(review.getCustomerEmail(), requesterEmail)) {
                throw new SecurityException("Not authorized to delete this review");
            }
        }
        repo.deleteById(id);
    }
}
