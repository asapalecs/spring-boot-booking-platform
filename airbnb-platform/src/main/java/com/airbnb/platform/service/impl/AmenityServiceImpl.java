package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.AmenityDTO;
import com.airbnb.platform.entity.Amenity;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.AmenityRepository;
import com.airbnb.platform.service.AmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;

    @Override
    public Amenity createAmenity(AmenityDTO amenityDTO) {
        log.info("Creez facilitate: {}", amenityDTO.getName());

        if (amenityRepository.existsByName(amenityDTO.getName())) {
            log.warn("Facilitatea {} exista deja", amenityDTO.getName());
            throw new IllegalArgumentException("Facilitatea cu acest nume exista deja");
        }

        Amenity amenity = new Amenity();
        amenity.setName(amenityDTO.getName());
        amenity.setDescription(amenityDTO.getDescription());

        return amenityRepository.save(amenity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> getAmenityById(Long id) {
        log.debug("Caut facilitate cu ID: {}", id);
        return amenityRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Amenity> findByName(String name) {
        log.debug("Caut facilitate dupa nume: {}", name);
        return amenityRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAllAmenities() {
        log.debug("Listez toate facilitatile");
        return amenityRepository.findAll();
    }

    @Override
    public Amenity updateAmenity(Long id, AmenityDTO amenityDTO) {
        log.info("Actualizez facilitate: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Facilitatea cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Facilitatea nu a fost gasita cu id: " + id);
                });

        if (amenityDTO.getName() != null && !amenityDTO.getName().equals(amenity.getName())) {
            if (amenityRepository.existsByName(amenityDTO.getName())) {
                log.warn("Facilitatea {} exista deja", amenityDTO.getName());
                throw new IllegalArgumentException("Facilitatea cu acest nume exista deja");
            }
            amenity.setName(amenityDTO.getName());
        }

        if (amenityDTO.getDescription() != null) {
            amenity.setDescription(amenityDTO.getDescription());
        }

        return amenityRepository.save(amenity);
    }

    @Override
    public void deleteAmenity(Long id) {
        log.info("Sterg facilitate: {}", id);
        var amenity = amenityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Facilitatea cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Facilitatea nu a fost gasita cu id: " + id);
                });
        // Proprietatea detine relatia, deci legaturile se scot inainte de stergere
        amenity.getProperties().forEach(property -> property.getAmenities().remove(amenity));
        amenityRepository.delete(amenity);
    }

    @Override
    public AmenityDTO convertToDTO(Amenity amenity) {
        return AmenityDTO.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .build();
    }

    @Override
    public Amenity convertToEntity(AmenityDTO amenityDTO) {
        Amenity amenity = new Amenity();
        amenity.setId(amenityDTO.getId());
        amenity.setName(amenityDTO.getName());
        amenity.setDescription(amenityDTO.getDescription());
        return amenity;
    }
}
