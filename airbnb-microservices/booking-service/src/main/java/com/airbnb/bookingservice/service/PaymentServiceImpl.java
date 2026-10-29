package com.airbnb.bookingservice.service;

import com.airbnb.bookingservice.dto.PaymentDTO;
import com.airbnb.bookingservice.entity.Payment;
import com.airbnb.bookingservice.entity.Reservation;
import com.airbnb.bookingservice.repository.PaymentRepository;
import com.airbnb.bookingservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public PaymentDTO processPayment(Long reservationId, String paymentMethod) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Rezervarea nu a fost gasita"));

        Optional<Payment> existingPayment = paymentRepository.findByReservationId(reservationId);
        if (existingPayment.isPresent()) {
            throw new IllegalArgumentException("Plata a fost deja procesata pentru aceasta rezervare");
        }

        String transactionId = generateTransactionId();

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .paymentMethod(paymentMethod)
                .transactionId(transactionId)
                .status(Payment.PaymentStatus.COMPLETED)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return PaymentDTO.fromEntity(savedPayment);
    }

    @Override
    public Optional<PaymentDTO> getPaymentByReservation(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .map(PaymentDTO::fromEntity);
    }

    @Override
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Plata nu a fost gasita"));

        if (!payment.getStatus().equals(Payment.PaymentStatus.COMPLETED)) {
            throw new IllegalArgumentException("Doar platile finalizate pot fi rambursate");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);
        return PaymentDTO.fromEntity(refundedPayment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
