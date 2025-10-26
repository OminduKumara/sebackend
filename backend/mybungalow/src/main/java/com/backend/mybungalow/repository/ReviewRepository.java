package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
}
