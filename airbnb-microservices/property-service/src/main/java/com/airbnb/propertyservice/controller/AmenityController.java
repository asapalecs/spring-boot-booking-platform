package com.airbnb.propertyservice.controller;

import com.airbnb.propertyservice.dto.AmenityDTO;
import com.airbnb.propertyservice.service.AmenityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@CrossOrigin(origins = "*")
public class AmenityController {
    @Autowired
    private AmenityService amenityService;

    @PostMapping
    public ResponseEntity<AmenityDTO> createAmenity(@Valid @RequestBody AmenityDTO amenityDTO) {
        AmenityDTO created = amenityService.createAmenity(amenityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmenityDTO> getAmenity(@PathVariable Long id) {
        return amenityService.getAmenityById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AmenityDTO>> getAllAmenities() {
        List<AmenityDTO> amenities = amenityService.getAllAmenities();
        return ResponseEntity.ok(amenities);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AmenityDTO> updateAmenity(@PathVariable Long id, @Valid @RequestBody AmenityDTO amenityDTO) {
        AmenityDTO updated = amenityService.updateAmenity(id, amenityDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
