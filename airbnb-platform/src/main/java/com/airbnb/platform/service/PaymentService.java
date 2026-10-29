package com.airbnb.platform.service;

import com.airbnb.platform.dto.PaymentDTO;
import com.airbnb.platform.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(PaymentDTO paymentDTO);

    Optional<Payment> getPaymentById(Long id);

    Optional<Payment> findByReservation(Long reservationId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(String status);

    List<Payment> getAllPayments();

    Payment updatePayment(Long id, PaymentDTO paymentDTO);

    void deletePayment(Long id);

    PaymentDTO convertToDTO(Payment payment);

    Payment convertToEntity(PaymentDTO paymentDTO);
}
