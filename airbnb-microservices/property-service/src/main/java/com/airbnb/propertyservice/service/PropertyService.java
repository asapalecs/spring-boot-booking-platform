package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.PropertyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    PropertyDTO createProperty(PropertyDTO propertyDTO);
    Optional<PropertyDTO> getPropertyById(Long id);
    Page<PropertyDTO> getAllProperties(Pageable pageable);
    Page<PropertyDTO> searchByCity(String city, Pageable pageable);
    Page<PropertyDTO> searchByCountry(String country, Pageable pageable);
    Page<PropertyDTO> searchByPrice(Double minPrice, Double maxPrice, Pageable pageable);
    Page<PropertyDTO> searchByCityAndPrice(String city, Double minPrice, Double maxPrice, Pageable pageable);
    List<PropertyDTO> getPropertiesByHost(Long hostId);
    PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO);
    void deleteProperty(Long id);
}
