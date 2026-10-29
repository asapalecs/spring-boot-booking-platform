package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.ReservationDTO;
import com.airbnb.platform.entity.Reservation;
import com.airbnb.platform.exception.InvalidRequestException;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.ReservationRepository;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Override
    public Reservation createReservation(ReservationDTO reservationDTO) {
        log.info("Creez rezervare pentru proprietate: {}", reservationDTO.getPropertyId());

        if (reservationDTO.getGuestId() == null || reservationDTO.getPropertyId() == null) {
            log.error("Rezervarea trebuie sa aiba oaspete si proprietate");
            throw new InvalidRequestException("Rezervarea trebuie sa aiba oaspete si proprietate");
        }

        if (!reservationDTO.getCheckInDate().isBefore(reservationDTO.getCheckOutDate())) {
            throw new InvalidRequestException("Data de check-out trebuie sa fie dupa check-in");
        }

        userRepository.findById(reservationDTO.getGuestId())
                .orElseThrow(() -> {
                    log.error("Oaspetelui nu a fost gasit");
                    return new ResourceNotFoundException("Oaspetelui nu a fost gasit");
                });

        var property = propertyRepository.findById(reservationDTO.getPropertyId())
                .orElseThrow(() -> {
                    log.error("Proprietatea nu a fost gasita");
                    return new ResourceNotFoundException("Proprietatea nu a fost gasita");
                });

        if (hasConflictingReservations(reservationDTO.getPropertyId(),
                                      reservationDTO.getCheckInDate(),
                                      reservationDTO.getCheckOutDate())) {
            log.warn("Proprietatea are rezervari conflictuale pentru datele selectate");
            throw new InvalidRequestException("Proprietatea are rezervari conflictuale pentru datele selectate");
        }

        Reservation reservation = new Reservation();
        reservation.setGuest(userRepository.findById(reservationDTO.getGuestId()).orElse(null));
        reservation.setProperty(property);
        reservation.setCheckInDate(reservationDTO.getCheckInDate());
        reservation.setCheckOutDate(reservationDTO.getCheckOutDate());
        reservation.setNumberOfGuests(reservationDTO.getNumberOfGuests() > 0 ? reservationDTO.getNumberOfGuests() : 1);
        reservation.setStatus("PENDING");

        long numberOfNights = ChronoUnit.DAYS.between(
            reservationDTO.getCheckInDate(),
            reservationDTO.getCheckOutDate()
        );
        BigDecimal totalPrice = property.getPricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
        reservation.setTotalPrice(totalPrice);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        log.debug("Caut rezervare cu ID: {}", id);
        return reservationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Reservation> findByGuest(Long guestId, Pageable pageable) {
        log.debug("Caut rezervari ale oaspetelui: {}", guestId);
        return reservationRepository.findByGuestId(guestId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Reservation> findByProperty(Long propertyId, Pageable pageable) {
        log.debug("Caut rezervari pentru proprietate: {}", propertyId);
        return reservationRepository.findByPropertyId(propertyId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Reservation> getAllReservations(Pageable pageable) {
        log.debug("Listez toate rezervarile");
        return reservationRepository.findAll(pageable);
    }

    @Override
    public Reservation confirmReservation(Long id) {
        log.info("Confirm rezervare: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rezervarea cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Rezervarea nu a fost gasita cu id: " + id);
                });
        reservation.setStatus("CONFIRMED");
        return reservationRepository.save(reservation);
    }

    @Override
    public void cancelReservation(Long id) {
        log.info("Anulez rezervare: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rezervarea cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Rezervarea nu a fost gasita cu id: " + id);
                });
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConflictingReservations(Long propertyId, LocalDate checkInDate, LocalDate checkOutDate) {
        log.debug("Verific rezervari conflictuale pentru proprietate: {} intre {} si {}",
                  propertyId, checkInDate, checkOutDate);
        var conflicts = reservationRepository.findOverlappingReservations(
                propertyId, checkInDate, checkOutDate);
        return !conflicts.isEmpty();
    }

    @Override
    public ReservationDTO convertToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .guestId(reservation.getGuest().getId())
                .propertyId(reservation.getProperty().getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .build();
    }

    @Override
    public Reservation convertToEntity(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDTO.getId());
        reservation.setCheckInDate(reservationDTO.getCheckInDate());
        reservation.setCheckOutDate(reservationDTO.getCheckOutDate());
        reservation.setStatus(reservationDTO.getStatus());
        reservation.setTotalPrice(reservationDTO.getTotalPrice());
        return reservation;
    }
}
