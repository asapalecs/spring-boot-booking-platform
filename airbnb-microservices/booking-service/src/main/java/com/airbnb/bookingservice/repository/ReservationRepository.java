package com.airbnb.bookingservice.repository;

import com.airbnb.bookingservice.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByGuestId(Long guestId, Pageable pageable);
    List<Reservation> findByPropertyId(Long propertyId);

    @Query("SELECT r FROM Reservation r WHERE r.propertyId = :propertyId " +
            "AND r.status IN ('PENDING', 'CONFIRMED') " +
            "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    List<Reservation> findConflictingReservations(
            @Param("propertyId") Long propertyId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
