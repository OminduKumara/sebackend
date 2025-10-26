package com.backend.mybungalow.controller;

import com.backend.mybungalow.model.Review;
import com.backend.mybungalow.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Public: list reviews (name + message + createdAt + admin reply)
    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> listPublic() {
        return ResponseEntity.ok(reviewService.listPublic());
    }

    // Customer: create review (must be authenticated as customer)
    public record CreateReviewRequest(String name, String email, String message) {}

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @PostMapping("/reviews")
    public ResponseEntity<Review> create(@RequestBody CreateReviewRequest req) {
        if (req == null || req.name() == null || req.name().isBlank() || req.message() == null || req.message().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        var saved = reviewService.create(req.name().trim(), req.email() == null ? null : req.email().trim(), req.message().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Admin: list all details
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reviews")
    public ResponseEntity<List<Review>> listAdmin() {
        return ResponseEntity.ok(reviewService.listAdmin());
    }

    // Admin: reply
    public record ReplyRequest(String reply) {}

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reviews/{id}/reply")
    public ResponseEntity<Review> reply(@PathVariable Long id, @RequestBody ReplyRequest req) {
        if (req == null || req.reply() == null || req.reply().isBlank()) return ResponseEntity.badRequest().build();
        var updated = reviewService.reply(id, req.reply().trim());
        return ResponseEntity.ok(updated);
    }

    // Admin: delete/clear reply
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/reviews/{id}/reply")
    public ResponseEntity<Void> deleteReply(@PathVariable Long id) {
        reviewService.clearReply(id);
        return ResponseEntity.noContent().build();
    }

    // Customer: list own reviews (requires authenticated customer)
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @GetMapping("/customers/reviews")
    public ResponseEntity<List<Review>> myReviews(Authentication authentication) {
        String email = null;
        if (authentication != null && authentication.getName() != null) {
            email = authentication.getName();
        }
        return ResponseEntity.ok(reviewService.listByCustomerEmail(email));
    }

    // Update own review message (customers) or any (admin)
    public record UpdateReviewRequest(String message) {}

    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody UpdateReviewRequest req, Authentication auth) {
        if (req == null || req.message() == null || req.message().isBlank()) return ResponseEntity.badRequest().build();
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = auth != null ? auth.getName() : null;
        var updated = reviewService.updateMessage(id, req.message().trim(), email, isAdmin);
        return ResponseEntity.ok(updated);
    }

    // Delete own review (customers) or any (admin)
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String email = auth != null ? auth.getName() : null;
        reviewService.deleteReview(id, email, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
