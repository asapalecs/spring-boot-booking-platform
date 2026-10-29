package com.airbnb.reviewservice.service;

import com.airbnb.reviewservice.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);
    Optional<ReviewDTO> getReviewById(Long id);
    Page<ReviewDTO> getReviewsByProperty(Long propertyId, Pageable pageable);
    List<ReviewDTO> getReviewsByUser(Long userId);
    Double getAverageRating(Long propertyId);
    Long getReviewCount(Long propertyId);
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
}
