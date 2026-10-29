package com.airbnb.platform.service;

import com.airbnb.platform.dto.AmenityDTO;
import com.airbnb.platform.entity.Amenity;

import java.util.List;
import java.util.Optional;

public interface AmenityService {
    Amenity createAmenity(AmenityDTO amenityDTO);

    Optional<Amenity> getAmenityById(Long id);

    Optional<Amenity> findByName(String name);

    List<Amenity> getAllAmenities();

    Amenity updateAmenity(Long id, AmenityDTO amenityDTO);

    void deleteAmenity(Long id);

    AmenityDTO convertToDTO(Amenity amenity);

    Amenity convertToEntity(AmenityDTO amenityDTO);
}
