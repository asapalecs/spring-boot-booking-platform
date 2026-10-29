package com.airbnb.bookingservice.controller;

import com.airbnb.bookingservice.dto.PaymentDTO;
import com.airbnb.bookingservice.dto.ReservationDTO;
import com.airbnb.bookingservice.dto.ReservationRequest;
import com.airbnb.bookingservice.service.PaymentService;
import com.airbnb.bookingservice.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDTO reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDTO>> getReservations(
            @RequestParam Long guestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationDTO> reservations = reservationService.getReservationsByGuest(guestId, pageable);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        ReservationDTO confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(confirmed);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        ReservationDTO cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(cancelled);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {
        PaymentDTO payment = paymentService.processPayment(id, paymentMethod);
        ReservationDTO reservation = reservationService.confirmReservation(id);

        Map<String, Object> response = new HashMap<>();
        response.put("reservation", reservation);
        response.put("payment", payment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
