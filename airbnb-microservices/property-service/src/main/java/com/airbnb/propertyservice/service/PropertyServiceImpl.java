package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.PropertyDTO;
import com.airbnb.propertyservice.entity.Property;
import com.airbnb.propertyservice.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public PropertyDTO createProperty(PropertyDTO propertyDTO) {
        Property property = Property.builder()
                .name(propertyDTO.getName())
                .description(propertyDTO.getDescription())
                .address(propertyDTO.getAddress())
                .city(propertyDTO.getCity())
                .country(propertyDTO.getCountry())
                .pricePerNight(propertyDTO.getPricePerNight())
                .maxGuests(propertyDTO.getMaxGuests())
                .propertyType(propertyDTO.getPropertyType())
                .available(propertyDTO.getAvailable())
                .hostId(propertyDTO.getHostId())
                .build();

        Property saved = propertyRepository.save(property);
        return PropertyDTO.fromEntity(saved);
    }

    @Override
    public Optional<PropertyDTO> getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public Page<PropertyDTO> getAllProperties(Pageable pageable) {
        return propertyRepository.findAll(pageable)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public Page<PropertyDTO> searchByCity(String city, Pageable pageable) {
        return propertyRepository.findByCity(city, pageable)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public Page<PropertyDTO> searchByCountry(String country, Pageable pageable) {
        return propertyRepository.findByCountry(country, pageable)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public Page<PropertyDTO> searchByPrice(Double minPrice, Double maxPrice, Pageable pageable) {
        return propertyRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public Page<PropertyDTO> searchByCityAndPrice(String city, Double minPrice, Double maxPrice, Pageable pageable) {
        return propertyRepository.findByCityAndPriceBetween(city, minPrice, maxPrice, pageable)
                .map(PropertyDTO::fromEntity);
    }

    @Override
    public List<PropertyDTO> getPropertiesByHost(Long hostId) {
        return propertyRepository.findByHostId(hostId).stream()
                .map(PropertyDTO::fromEntity)
                .toList();
    }

    @Override
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proprietatea nu a fost gasita"));

        if (propertyDTO.getName() != null) {
            property.setName(propertyDTO.getName());
        }
        if (propertyDTO.getDescription() != null) {
            property.setDescription(propertyDTO.getDescription());
        }
        if (propertyDTO.getPricePerNight() != null) {
            property.setPricePerNight(propertyDTO.getPricePerNight());
        }
        if (propertyDTO.getAvailable() != null) {
            property.setAvailable(propertyDTO.getAvailable());
        }

        Property updated = propertyRepository.save(property);
        return PropertyDTO.fromEntity(updated);
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}
