package com.airbnb.platform.service;

import com.airbnb.platform.dto.ReservationDTO;
import com.airbnb.platform.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(ReservationDTO reservationDTO);

    Optional<Reservation> getReservationById(Long id);

    Page<Reservation> findByGuest(Long guestId, Pageable pageable);

    Page<Reservation> findByProperty(Long propertyId, Pageable pageable);

    Page<Reservation> getAllReservations(Pageable pageable);

    Reservation confirmReservation(Long id);

    void cancelReservation(Long id);

    boolean hasConflictingReservations(Long propertyId, LocalDate checkInDate, LocalDate checkOutDate);

    ReservationDTO convertToDTO(Reservation reservation);

    Reservation convertToEntity(ReservationDTO reservationDTO);
}
