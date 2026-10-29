package com.airbnb.platform.service;

import com.airbnb.platform.dto.ReviewDTO;
import com.airbnb.platform.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewService {
    Review createReview(ReviewDTO reviewDTO);

    Optional<Review> getReviewById(Long id);

    Page<Review> findByProperty(Long propertyId, Pageable pageable);

    Page<Review> findByUser(Long userId, Pageable pageable);

    Page<Review> getAllReviews(Pageable pageable);

    Double getAverageRating(Long propertyId);

    Review updateReview(Long id, ReviewDTO reviewDTO);

    void deleteReview(Long id);

    ReviewDTO convertToDTO(Review review);

    Review convertToEntity(ReviewDTO reviewDTO);
}
