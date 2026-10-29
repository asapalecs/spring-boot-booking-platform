package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Override
    @EntityGraph(attributePaths = {"property", "guest"})
    Page<Reservation> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"property", "property.host", "guest"})
    Optional<Reservation> findById(Long id);

    List<Reservation> findByGuestId(Long guestId);

    @EntityGraph(attributePaths = {"property", "guest"})
    Page<Reservation> findByGuestId(Long guestId, Pageable pageable);

    List<Reservation> findByPropertyId(Long propertyId);

    Page<Reservation> findByPropertyId(Long propertyId, Pageable pageable);

    List<Reservation> findByStatus(String status);

    @Query("SELECT r FROM Reservation r WHERE r.property.id = :propertyId AND " +
           "r.status != 'CANCELLED' AND " +
           "r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate")
    List<Reservation> findOverlappingReservations(@Param("propertyId") Long propertyId,
                                                   @Param("checkInDate") LocalDate checkInDate,
                                                   @Param("checkOutDate") LocalDate checkOutDate);
}
