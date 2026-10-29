package com.airbnb.propertyservice.repository;

import com.airbnb.propertyservice.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByCity(String city);
    List<Property> findByCountry(String country);
    Page<Property> findByCity(String city, Pageable pageable);
    Page<Property> findByCountry(String country, Pageable pageable);
    List<Property> findByHostId(Long hostId);

    @Query("SELECT p FROM Property p WHERE p.pricePerNight BETWEEN :minPrice AND :maxPrice")
    Page<Property> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.city = :city AND p.pricePerNight BETWEEN :minPrice AND :maxPrice")
    Page<Property> findByCityAndPriceBetween(@Param("city") String city, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
}
