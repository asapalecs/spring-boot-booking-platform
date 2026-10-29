package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    @Override
    @EntityGraph(attributePaths = {"host", "amenities"})
    Optional<Property> findById(Long id);

    List<Property> findByCity(String city);

    Page<Property> findByCity(String city, Pageable pageable);

    Page<Property> findByCountry(String country, Pageable pageable);

    List<Property> findByHostId(Long hostId);

    Page<Property> findByHostId(Long hostId, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Property> searchByNameOrCity(@Param("keyword") String keyword, Pageable pageable);

    List<Property> findByAvailableTrue();

    Page<Property> findByAvailableTrue(Pageable pageable);

    List<Property> findByMaxGuestsGreaterThanEqual(int maxGuests);

    List<Property> findByPricePerNightBetween(BigDecimal min, BigDecimal max);

    Page<Property> findByPricePerNightBetween(BigDecimal min, BigDecimal max, Pageable pageable);
}
