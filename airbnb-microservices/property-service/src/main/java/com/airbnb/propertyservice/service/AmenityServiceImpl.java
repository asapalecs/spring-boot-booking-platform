package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.AmenityDTO;
import com.airbnb.propertyservice.entity.Amenity;
import com.airbnb.propertyservice.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AmenityServiceImpl implements AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;

    @Override
    public AmenityDTO createAmenity(AmenityDTO amenityDTO) {
        Amenity amenity = Amenity.builder()
                .name(amenityDTO.getName())
                .description(amenityDTO.getDescription())
                .icon(amenityDTO.getIcon())
                .build();

        Amenity saved = amenityRepository.save(amenity);
        return AmenityDTO.fromEntity(saved);
    }

    @Override
    public Optional<AmenityDTO> getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .map(AmenityDTO::fromEntity);
    }

    @Override
    public List<AmenityDTO> getAllAmenities() {
        return amenityRepository.findAll().stream()
                .map(AmenityDTO::fromEntity)
                .toList();
    }

    @Override
    public AmenityDTO updateAmenity(Long id, AmenityDTO amenityDTO) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facilitatea nu a fost gasita"));

        if (amenityDTO.getDescription() != null) {
            amenity.setDescription(amenityDTO.getDescription());
        }
        if (amenityDTO.getIcon() != null) {
            amenity.setIcon(amenityDTO.getIcon());
        }

        Amenity updated = amenityRepository.save(amenity);
        return AmenityDTO.fromEntity(updated);
    }

    @Override
    public void deleteAmenity(Long id) {
        amenityRepository.deleteById(id);
    }
}
