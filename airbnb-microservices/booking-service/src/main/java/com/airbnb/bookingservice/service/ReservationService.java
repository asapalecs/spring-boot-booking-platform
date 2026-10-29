package com.airbnb.bookingservice.service;

import com.airbnb.bookingservice.dto.ReservationDTO;
import com.airbnb.bookingservice.dto.ReservationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReservationService {
    ReservationDTO createReservation(ReservationRequest request);
    Optional<ReservationDTO> getReservationById(Long id);
    Page<ReservationDTO> getReservationsByGuest(Long guestId, Pageable pageable);
    ReservationDTO confirmReservation(Long id);
    ReservationDTO cancelReservation(Long id);
    void deleteReservation(Long id);
}
