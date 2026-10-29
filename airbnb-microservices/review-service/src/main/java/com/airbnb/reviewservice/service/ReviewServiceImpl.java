package com.airbnb.reviewservice.service;

import com.airbnb.reviewservice.dto.ReviewDTO;
import com.airbnb.reviewservice.entity.Review;
import com.airbnb.reviewservice.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = Review.builder()
                .userId(reviewDTO.getUserId())
                .propertyId(reviewDTO.getPropertyId())
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewDTO.fromEntity(saved);
    }

    @Override
    public Optional<ReviewDTO> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewDTO::fromEntity);
    }

    @Override
    public Page<ReviewDTO> getReviewsByProperty(Long propertyId, Pageable pageable) {
        return reviewRepository.findByPropertyId(propertyId, pageable)
                .map(ReviewDTO::fromEntity);
    }

    @Override
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(ReviewDTO::fromEntity)
                .toList();
    }

    @Override
    public Double getAverageRating(Long propertyId) {
        return reviewRepository.getAverageRatingByProperty(propertyId);
    }

    @Override
    public Long getReviewCount(Long propertyId) {
        return reviewRepository.getReviewCountByProperty(propertyId);
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recenzia nu a fost gasita"));

        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getComment() != null) {
            review.setComment(reviewDTO.getComment());
        }

        Review updated = reviewRepository.save(review);
        return ReviewDTO.fromEntity(updated);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
