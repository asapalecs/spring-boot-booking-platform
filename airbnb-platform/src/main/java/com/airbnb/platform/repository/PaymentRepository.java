package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Override
    @EntityGraph(attributePaths = {"reservation", "reservation.guest", "reservation.property"})
    Optional<Payment> findById(Long id);

    @EntityGraph(attributePaths = {"reservation", "reservation.guest"})
    List<Payment> findAll();

    Optional<Payment> findByReservationId(Long reservationId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(String status);
}
