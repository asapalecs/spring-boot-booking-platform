package com.airbnb.bookingservice.service;

import com.airbnb.bookingservice.client.PropertyServiceClient;
import com.airbnb.bookingservice.dto.ReservationDTO;
import com.airbnb.bookingservice.dto.ReservationRequest;
import com.airbnb.bookingservice.entity.Reservation;
import com.airbnb.bookingservice.repository.ReservationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PropertyServiceClient propertyServiceClient;

    @Override
    @CircuitBreaker(name = "property-service", fallbackMethod = "createReservationFallback")
    public ReservationDTO createReservation(ReservationRequest request) {
        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException("Data de check-in trebuie sa fie inainte de data de check-out");
        }

        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getPropertyId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Proprietatea nu este disponibila pentru datele selectate");
        }

        PropertyServiceClient.PropertyInfo property = propertyServiceClient.getProperty(request.getPropertyId());

        if (!property.available) {
            throw new IllegalArgumentException("Proprietatea nu este disponibila");
        }

        if (request.getNumberOfGuests() > property.maxGuests) {
            throw new IllegalArgumentException("Numarul de oaspeti depaseste capacitatea proprietatii");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = nights * property.pricePerNight;

        Reservation reservation = Reservation.builder()
                .guestId(request.getGuestId())
                .propertyId(request.getPropertyId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(totalPrice)
                .status(Reservation.ReservationStatus.PENDING)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(saved);
    }

    public ReservationDTO createReservationFallback(ReservationRequest request, Exception ex) {
        if (ex instanceof IllegalArgumentException invalid) {
            throw invalid;
        }
        logger.warn("Serviciul de proprietati este indisponibil, se foloseste fallback pentru crearea rezervarii", ex);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Serviciul de proprietati este temporar indisponibil. Incercati din nou mai tarziu.");
    }

    @Override
    public Optional<ReservationDTO> getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(ReservationDTO::fromEntity);
    }

    @Override
    public Page<ReservationDTO> getReservationsByGuest(Long guestId, Pageable pageable) {
        return reservationRepository.findByGuestId(guestId, pageable)
                .map(ReservationDTO::fromEntity);
    }

    @Override
    public ReservationDTO confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rezervarea nu a fost gasita"));

        if (!reservation.getStatus().equals(Reservation.ReservationStatus.PENDING)) {
            throw new IllegalArgumentException("Doar rezervarile in asteptare pot fi confirmate");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        Reservation updated = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(updated);
    }

    @Override
    public ReservationDTO cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rezervarea nu a fost gasita"));

        if (reservation.getStatus().equals(Reservation.ReservationStatus.COMPLETED) ||
            reservation.getStatus().equals(Reservation.ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("Nu se poate anula rezervarea cu statusul: " + reservation.getStatus());
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);
        return ReservationDTO.fromEntity(updated);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}
