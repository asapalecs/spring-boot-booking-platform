package com.airbnb.platform.controller;

import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.dto.ReservationDTO;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class RestApiController {
    private final PropertyService propertyService;
    private final ReservationService reservationService;

    @GetMapping("/properties")
    public ResponseEntity<Page<PropertyDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Listez proprietati");
        Pageable pageable = PageRequest.of(page, size);
        var properties = propertyService.getAllProperties(pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        log.info("API: Caut proprietate cu ID: {}", id);
        return propertyService.getPropertyById(id)
                .map(property -> ResponseEntity.ok(propertyService.convertToDTO(property)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/properties/search")
    public ResponseEntity<Page<PropertyDTO>> searchProperties(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut proprietati dupa cuvant cheie: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);
        var results = propertyService.searchProperties(keyword, pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/properties/city/{city}")
    public ResponseEntity<Page<PropertyDTO>> getPropertiesByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut proprietati in oras: {}", city);
        Pageable pageable = PageRequest.of(page, size);
        var properties = propertyService.findByCity(city, pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/country/{country}")
    public ResponseEntity<Page<PropertyDTO>> getPropertiesByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut proprietati in tara: {}", country);
        Pageable pageable = PageRequest.of(page, size);
        var properties = propertyService.findByCountry(country, pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/available")
    public ResponseEntity<Page<PropertyDTO>> getAvailableProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Listez proprietati disponibile");
        Pageable pageable = PageRequest.of(page, size);
        var properties = propertyService.findAvailable(pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/properties/price-range")
    public ResponseEntity<Page<PropertyDTO>> getPropertiesByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut proprietati in interval de pret: {} - {}", min, max);
        Pageable pageable = PageRequest.of(page, size);
        var properties = propertyService.findByPriceRange(min, max, pageable)
                .map(propertyService::convertToDTO);
        return ResponseEntity.ok(properties);
    }

    @PostMapping("/properties")
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) {
        log.info("API: Creez proprietate: {}", propertyDTO.getName());
        try {
            var property = propertyService.convertToEntity(propertyDTO);
            var createdProperty = propertyService.createProperty(property);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(propertyService.convertToDTO(createdProperty));
        } catch (Exception e) {
            log.error("Eroare la crearea proprietatii", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/properties/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable Long id,
                                                      @Valid @RequestBody PropertyDTO propertyDTO) {
        log.info("API: Actualizez proprietate: {}", id);
        try {
            var updatedProperty = propertyService.updateProperty(id, propertyDTO);
            return ResponseEntity.ok(propertyService.convertToDTO(updatedProperty));
        } catch (Exception e) {
            log.error("Eroare la actualizarea proprietatii", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        log.info("API: Sterg proprietate: {}", id);
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Eroare la stergerea proprietatii", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<Page<ReservationDTO>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Listez rezervari");
        Pageable pageable = PageRequest.of(page, size);
        var reservations = reservationService.getAllReservations(pageable)
                .map(reservationService::convertToDTO);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        log.info("API: Caut rezervare cu ID: {}", id);
        return reservationService.getReservationById(id)
                .map(reservation -> ResponseEntity.ok(reservationService.convertToDTO(reservation)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reservations/guest/{guestId}")
    public ResponseEntity<Page<ReservationDTO>> getReservationsByGuest(
            @PathVariable Long guestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut rezervari ale oaspetelui: {}", guestId);
        Pageable pageable = PageRequest.of(page, size);
        var reservations = reservationService.findByGuest(guestId, pageable)
                .map(reservationService::convertToDTO);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/reservations/property/{propertyId}")
    public ResponseEntity<Page<ReservationDTO>> getReservationsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("API: Caut rezervari pentru proprietate: {}", propertyId);
        Pageable pageable = PageRequest.of(page, size);
        var reservations = reservationService.findByProperty(propertyId, pageable)
                .map(reservationService::convertToDTO);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        log.info("API: Creez rezervare pentru proprietate: {}", reservationDTO.getPropertyId());
        try {
            var reservation = reservationService.createReservation(reservationDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reservationService.convertToDTO(reservation));
        } catch (Exception e) {
            log.error("Eroare la crearea rezervarii", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reservations/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        log.info("API: Confirm rezervare: {}", id);
        try {
            var reservation = reservationService.confirmReservation(id);
            return ResponseEntity.ok(reservationService.convertToDTO(reservation));
        } catch (Exception e) {
            log.error("Eroare la confirmarea rezervarii", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/reservations/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        log.info("API: Anulez rezervare: {}", id);
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Eroare la anularea rezervarii", e);
            return ResponseEntity.notFound().build();
        }
    }
}
