package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.AmenityDTO;

import java.util.List;
import java.util.Optional;

public interface AmenityService {
    AmenityDTO createAmenity(AmenityDTO amenityDTO);
    Optional<AmenityDTO> getAmenityById(Long id);
    List<AmenityDTO> getAllAmenities();
    AmenityDTO updateAmenity(Long id, AmenityDTO amenityDTO);
    void deleteAmenity(Long id);
}
