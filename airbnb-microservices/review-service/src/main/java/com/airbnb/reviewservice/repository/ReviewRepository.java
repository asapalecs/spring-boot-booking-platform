package com.airbnb.reviewservice.repository;

import com.airbnb.reviewservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPropertyId(Long propertyId, Pageable pageable);
    List<Review> findByUserId(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.propertyId = :propertyId")
    Double getAverageRatingByProperty(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.propertyId = :propertyId")
    Long getReviewCountByProperty(@Param("propertyId") Long propertyId);
}
