package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Override
    @EntityGraph(attributePaths = {"user", "property"})
    Optional<Review> findById(Long id);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT r FROM Review r WHERE r.property.id = :propertyId")
    Page<Review> findByPropertyId(@Param("propertyId") Long propertyId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);
}
