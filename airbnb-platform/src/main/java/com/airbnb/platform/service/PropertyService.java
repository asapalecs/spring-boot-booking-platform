package com.airbnb.platform.service;

import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface PropertyService {
    Property createProperty(Property property);

    Optional<Property> getPropertyById(Long id);

    Page<Property> findByHost(Long hostId, Pageable pageable);

    Page<Property> searchProperties(String keyword, Pageable pageable);

    Page<Property> findByCity(String city, Pageable pageable);

    Page<Property> findByCountry(String country, Pageable pageable);

    Page<Property> findAvailable(Pageable pageable);

    Page<Property> findByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable);

    Page<Property> getAllProperties(Pageable pageable);

    Property updateProperty(Long id, PropertyDTO propertyDTO);

    void deleteProperty(Long id);

    void addAmenityToProperty(Long propertyId, Long amenityId);

    void removeAmenityFromProperty(Long propertyId, Long amenityId);

    PropertyDTO convertToDTO(Property property);

    Property convertToEntity(PropertyDTO propertyDTO);
}
