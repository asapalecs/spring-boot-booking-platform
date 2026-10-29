package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.PaymentDTO;
import com.airbnb.platform.entity.Payment;
import com.airbnb.platform.exception.InvalidRequestException;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.PaymentRepository;
import com.airbnb.platform.repository.ReservationRepository;
import com.airbnb.platform.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public Payment processPayment(PaymentDTO paymentDTO) {
        log.info("Procesez plata pentru rezervare: {}", paymentDTO.getReservationId());

        var reservation = reservationRepository.findById(paymentDTO.getReservationId())
                .orElseThrow(() -> {
                    log.error("Rezervarea nu a fost gasita");
                    return new ResourceNotFoundException("Rezervarea nu a fost gasita");
                });

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new InvalidRequestException("Doar rezervarile in asteptare pot fi platite");
        }
        if (paymentRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new InvalidRequestException("Aceasta rezervare a fost deja platita");
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        // Suma se ia din rezervare, nu din formular
        payment.setAmount(reservation.getTotalPrice());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTransactionId(UUID.randomUUID().toString());

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long id) {
        log.debug("Caut plata cu ID: {}", id);
        return paymentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findByReservation(Long reservationId) {
        log.debug("Caut plata pentru rezervare: {}", reservationId);
        return paymentRepository.findByReservationId(reservationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findByTransactionId(String transactionId) {
        log.debug("Caut plata dupa ID-ul tranzactiei: {}", transactionId);
        return paymentRepository.findByTransactionId(transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> findByStatus(String status) {
        log.debug("Caut plati cu status: {}", status);
        return paymentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        log.debug("Listez toate platile");
        return paymentRepository.findAll();
    }

    @Override
    public Payment updatePayment(Long id, PaymentDTO paymentDTO) {
        log.info("Actualizez plata: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Plata cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Plata nu a fost gasita cu id: " + id);
                });

        if (paymentDTO.getAmount() != null) {
            payment.setAmount(paymentDTO.getAmount());
        }
        if (paymentDTO.getStatus() != null) {
            payment.setStatus(paymentDTO.getStatus());
        }
        if (paymentDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        }

        return paymentRepository.save(payment);
    }

    @Override
    public void deletePayment(Long id) {
        log.info("Sterg plata: {}", id);
        if (!paymentRepository.existsById(id)) {
            log.error("Plata cu ID {} nu a fost gasita", id);
            throw new ResourceNotFoundException("Plata nu a fost gasita cu id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

    @Override
    public Payment convertToEntity(PaymentDTO paymentDTO) {
        Payment payment = new Payment();
        payment.setId(paymentDTO.getId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setStatus(paymentDTO.getStatus());
        payment.setTransactionId(paymentDTO.getTransactionId());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        return payment;
    }
}
