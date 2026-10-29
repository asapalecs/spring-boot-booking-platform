package com.airbnb.bookingservice.service;

import com.airbnb.bookingservice.dto.PaymentDTO;

import java.util.Optional;

public interface PaymentService {
    PaymentDTO processPayment(Long reservationId, String paymentMethod);
    Optional<PaymentDTO> getPaymentByReservation(Long reservationId);
    PaymentDTO refundPayment(Long paymentId);
}
