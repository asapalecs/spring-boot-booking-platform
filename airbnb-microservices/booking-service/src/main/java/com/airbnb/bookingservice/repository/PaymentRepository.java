package com.airbnb.bookingservice.repository;

import com.airbnb.bookingservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
    Optional<Payment> findByTransactionId(String transactionId);
}
