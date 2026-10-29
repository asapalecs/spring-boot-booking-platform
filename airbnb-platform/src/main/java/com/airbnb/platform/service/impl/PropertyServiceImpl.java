package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.entity.Amenity;
import com.airbnb.platform.entity.Property;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.AmenityRepository;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;

    @Override
    public Property createProperty(Property property) {
        log.info("Creez proprietate: {}", property.getName());
        if (property.getHost() == null || property.getHost().getId() == null) {
            log.error("Proprietatea trebuie sa aiba un gazda");
            throw new IllegalArgumentException("Proprietatea trebuie sa aiba un gazda");
        }
        userRepository.findById(property.getHost().getId())
                .orElseThrow(() -> {
                    log.error("Gazda nu a fost gasita");
                    return new ResourceNotFoundException("Gazda nu a fost gasita");
                });
        return propertyRepository.save(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Property> getPropertyById(Long id) {
        log.debug("Caut proprietate cu ID: {}", id);
        return propertyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findByHost(Long hostId, Pageable pageable) {
        log.debug("Caut proprietati ale gazdei: {}", hostId);
        return propertyRepository.findByHostId(hostId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> searchProperties(String keyword, Pageable pageable) {
        log.debug("Caut proprietati dupa cuvant cheie: {}", keyword);
        return propertyRepository.searchByNameOrCity(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findByCity(String city, Pageable pageable) {
        log.debug("Caut proprietati in oras: {}", city);
        return propertyRepository.findByCity(city, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findByCountry(String country, Pageable pageable) {
        log.debug("Caut proprietati in tara: {}", country);
        return propertyRepository.findByCountry(country, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findAvailable(Pageable pageable) {
        log.debug("Caut proprietati disponibile");
        return propertyRepository.findByAvailableTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> findByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
        log.debug("Caut proprietati in interval de pret: {} - {}", min, max);
        return propertyRepository.findByPricePerNightBetween(min, max, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Property> getAllProperties(Pageable pageable) {
        log.debug("Listez toate proprietatile");
        return propertyRepository.findAll(pageable);
    }

    @Override
    public Property updateProperty(Long id, PropertyDTO propertyDTO) {
        log.info("Actualizez proprietate: {}", id);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Proprietatea cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Proprietatea nu a fost gasita cu id: " + id);
                });

        if (propertyDTO.getName() != null) {
            property.setName(propertyDTO.getName());
        }
        if (propertyDTO.getDescription() != null) {
            property.setDescription(propertyDTO.getDescription());
        }
        if (propertyDTO.getCity() != null) {
            property.setCity(propertyDTO.getCity());
        }
        if (propertyDTO.getCountry() != null) {
            property.setCountry(propertyDTO.getCountry());
        }
        if (propertyDTO.getPricePerNight() != null) {
            property.setPricePerNight(propertyDTO.getPricePerNight());
        }
        if (propertyDTO.getAvailable() != null) {
            property.setAvailable(propertyDTO.getAvailable());
        }
        if (propertyDTO.getPropertyType() != null) {
            property.setPropertyType(propertyDTO.getPropertyType());
        }
        if (propertyDTO.getAddress() != null) {
            property.setAddress(propertyDTO.getAddress());
        }
        if (propertyDTO.getMaxGuests() > 0) {
            property.setMaxGuests(propertyDTO.getMaxGuests());
        }
        if (propertyDTO.getAmenityIds() != null) {
            property.getAmenities().clear();
            property.getAmenities().addAll(amenityRepository.findAllById(propertyDTO.getAmenityIds()));
        }

        return propertyRepository.save(property);
    }

    @Override
    public void deleteProperty(Long id) {
        log.info("Sterg proprietate: {}", id);
        if (!propertyRepository.existsById(id)) {
            log.error("Proprietatea cu ID {} nu a fost gasita", id);
            throw new ResourceNotFoundException("Proprietatea nu a fost gasita cu id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    @Override
    public void addAmenityToProperty(Long propertyId, Long amenityId) {
        log.info("Adaug facilitate {} la proprietate {}", amenityId, propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Proprietatea nu a fost gasita"));
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facilitatea nu a fost gasita"));

        property.getAmenities().add(amenity);
        propertyRepository.save(property);
    }

    @Override
    public void removeAmenityFromProperty(Long propertyId, Long amenityId) {
        log.info("Elimin facilitatea {} din proprietate {}", amenityId, propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Proprietatea nu a fost gasita"));
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facilitatea nu a fost gasita"));

        property.getAmenities().remove(amenity);
        propertyRepository.save(property);
    }

    @Override
    public PropertyDTO convertToDTO(Property property) {
        return PropertyDTO.builder()
                .id(property.getId())
                .hostId(property.getHost().getId())
                .name(property.getName())
                .description(property.getDescription())
                .propertyType(property.getPropertyType())
                .address(property.getAddress())
                .city(property.getCity())
                .country(property.getCountry())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .available(property.getAvailable())
                .build();
    }

    @Override
    public Property convertToEntity(PropertyDTO propertyDTO) {
        Property property = new Property();
        property.setId(propertyDTO.getId());
        property.setName(propertyDTO.getName());
        property.setDescription(propertyDTO.getDescription());
        property.setPropertyType(propertyDTO.getPropertyType());
        property.setAddress(propertyDTO.getAddress());
        property.setCity(propertyDTO.getCity());
        property.setCountry(propertyDTO.getCountry());
        property.setPricePerNight(propertyDTO.getPricePerNight());
        property.setMaxGuests(propertyDTO.getMaxGuests());
        property.setAvailable(propertyDTO.getAvailable() != null ? propertyDTO.getAvailable() : true);
        if (propertyDTO.getHostId() != null) {
            userRepository.findById(propertyDTO.getHostId()).ifPresent(property::setHost);
        }
        if (propertyDTO.getAmenityIds() != null) {
            property.setAmenities(new HashSet<>(amenityRepository.findAllById(propertyDTO.getAmenityIds())));
        }
        return property;
    }
}
