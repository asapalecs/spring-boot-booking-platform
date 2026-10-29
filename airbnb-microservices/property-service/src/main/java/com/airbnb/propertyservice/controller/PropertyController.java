package com.airbnb.propertyservice.controller;

import com.airbnb.propertyservice.dto.PropertyDTO;
import com.airbnb.propertyservice.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {
    @Autowired
    private PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) {
        PropertyDTO created = propertyService.createProperty(propertyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<PropertyDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDTO> properties = propertyService.getAllProperties(pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search/city")
    public ResponseEntity<Page<PropertyDTO>> searchByCity(
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDTO> properties = propertyService.searchByCity(city, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search/country")
    public ResponseEntity<Page<PropertyDTO>> searchByCountry(
            @RequestParam String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDTO> properties = propertyService.searchByCountry(country, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search/price")
    public ResponseEntity<Page<PropertyDTO>> searchByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDTO> properties = propertyService.searchByPrice(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search/city-price")
    public ResponseEntity<Page<PropertyDTO>> searchByCityAndPrice(
            @RequestParam String city,
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyDTO> properties = propertyService.searchByCityAndPrice(city, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByHost(@PathVariable Long hostId) {
        List<PropertyDTO> properties = propertyService.getPropertiesByHost(hostId);
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyDTO propertyDTO) {
        PropertyDTO updated = propertyService.updateProperty(id, propertyDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
