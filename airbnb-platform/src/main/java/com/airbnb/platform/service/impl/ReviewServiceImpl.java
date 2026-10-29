package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.ReviewDTO;
import com.airbnb.platform.entity.Review;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.ReviewRepository;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Override
    public Review createReview(ReviewDTO reviewDTO) {
        log.info("Creez recenzie pentru proprietate: {}", reviewDTO.getPropertyId());

        var property = propertyRepository.findById(reviewDTO.getPropertyId())
                .orElseThrow(() -> {
                    log.error("Proprietatea nu a fost gasita");
                    return new ResourceNotFoundException("Proprietatea nu a fost gasita");
                });

        var user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("Utilizatorul nu a fost gasit");
                    return new ResourceNotFoundException("Utilizatorul nu a fost gasit");
                });

        Review review = new Review();
        review.setProperty(property);
        review.setUser(user);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(Long id) {
        log.debug("Caut recenzie cu ID: {}", id);
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> findByProperty(Long propertyId, Pageable pageable) {
        log.debug("Caut recenzii pentru proprietate: {}", propertyId);
        return reviewRepository.findByPropertyId(propertyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> findByUser(Long userId, Pageable pageable) {
        log.debug("Caut recenzii de la utilizator: {}", userId);
        return reviewRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getAllReviews(Pageable pageable) {
        log.debug("Listez toate recenziile");
        return reviewRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long propertyId) {
        log.debug("Calculez media ratingurilor pentru proprietate: {}", propertyId);
        Double average = reviewRepository.findAverageRatingByPropertyId(propertyId);
        return average != null ? average : 0.0;
    }

    @Override
    public Review updateReview(Long id, ReviewDTO reviewDTO) {
        log.info("Actualizez recenzie: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recenzia cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Recenzia nu a fost gasita cu id: " + id);
                });

        if (reviewDTO.getRating() != null) {
            review.setRating(reviewDTO.getRating());
        }
        if (reviewDTO.getComment() != null) {
            review.setComment(reviewDTO.getComment());
        }

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        log.info("Sterg recenzie: {}", id);
        if (!reviewRepository.existsById(id)) {
            log.error("Recenzia cu ID {} nu a fost gasita", id);
            throw new ResourceNotFoundException("Recenzia nu a fost gasita cu id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    public ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .propertyId(review.getProperty().getId())
                .userId(review.getUser().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }

    @Override
    public Review convertToEntity(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setId(reviewDTO.getId());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        return review;
    }
}
