package com.airbnb.platform.controller;

import com.airbnb.platform.dto.PaymentDTO;
import com.airbnb.platform.entity.Reservation;
import com.airbnb.platform.service.PaymentService;
import com.airbnb.platform.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private static final Set<String> PAYMENT_METHODS = Set.of("CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER");

    private final PaymentService paymentService;
    private final ReservationService reservationService;

    @GetMapping
    public String listPayments(Model model) {
        log.info("Listez toate platile");
        model.addAttribute("payments", paymentService.getAllPayments());
        return "payment/list";
    }

    @GetMapping("/{id}")
    public String viewPayment(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails currentUser,
                              Model model) {
        log.info("Vizionez plata: {}", id);
        var payment = paymentService.getPaymentById(id);
        if (payment.isPresent() && (isAdmin(currentUser) || isGuest(payment.get().getReservation(), currentUser))) {
            model.addAttribute("payment", payment.get());
            return "payment/detail";
        }
        return "redirect:/reservations";
    }

    @GetMapping("/reservations/{reservationId}/pay")
    public String paymentForm(@PathVariable Long reservationId,
                              @AuthenticationPrincipal UserDetails currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        log.info("Afisez formular de plata pentru rezervare: {}", reservationId);
        var reservation = reservationService.getReservationById(reservationId);
        if (reservation.isEmpty() || !isGuest(reservation.get(), currentUser)) {
            return "redirect:/reservations";
        }
        if (!"PENDING".equals(reservation.get().getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Doar rezervarile in asteptare pot fi platite");
            return "redirect:/reservations/" + reservationId;
        }
        var payment = new PaymentDTO();
        payment.setReservationId(reservationId);
        payment.setAmount(reservation.get().getTotalPrice());
        payment.setPaymentMethod("CREDIT_CARD");

        model.addAttribute("payment", payment);
        model.addAttribute("reservation", reservation.get());
        return "payment/form";
    }

    @PostMapping
    public String processPayment(@ModelAttribute PaymentDTO paymentDTO,
                                @AuthenticationPrincipal UserDetails currentUser,
                                RedirectAttributes redirectAttributes) {
        Long reservationId = paymentDTO.getReservationId();
        log.info("Procesez plata pentru rezervare: {}", reservationId);
        var reservation = reservationId == null ? null : reservationService.getReservationById(reservationId).orElse(null);
        if (reservation == null || !isGuest(reservation, currentUser)) {
            return "redirect:/reservations";
        }
        if (!PAYMENT_METHODS.contains(paymentDTO.getPaymentMethod())) {
            redirectAttributes.addFlashAttribute("error", "Alege o metoda de plata valida");
            return "redirect:/payments/reservations/" + reservationId + "/pay";
        }
        try {
            var payment = paymentService.processPayment(paymentDTO);
            reservationService.confirmReservation(reservationId);

            redirectAttributes.addFlashAttribute("message", "Plata procesata cu succes. Rezervare confirmata.");
            return "redirect:/payments/" + payment.getId();
        } catch (Exception e) {
            log.error("Eroare la procesarea platii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/" + reservationId;
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelPayment(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        log.info("Anulez plata: {}", id);
        try {
            var payment = paymentService.getPaymentById(id).orElse(null);
            if (payment != null) {
                var updatedPayment = new PaymentDTO();
                updatedPayment.setStatus("CANCELLED");
                paymentService.updatePayment(id, updatedPayment);

                redirectAttributes.addFlashAttribute("message", "Plata anulata cu succes");
            }
        } catch (Exception e) {
            log.error("Eroare la anularea platii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments/" + id;
    }

    @PostMapping("/{id}/refund")
    public String refundPayment(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        log.info("Returnez bani pentru plata: {}", id);
        try {
            var payment = paymentService.getPaymentById(id).orElse(null);
            if (payment != null) {
                var updatedPayment = new PaymentDTO();
                updatedPayment.setStatus("REFUNDED");
                paymentService.updatePayment(id, updatedPayment);

                redirectAttributes.addFlashAttribute("message", "Rambursare procesata cu succes");
            }
        } catch (Exception e) {
            log.error("Eroare la rambursare", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/payments/" + id;
    }

    private boolean isAdmin(UserDetails currentUser) {
        return currentUser.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean isGuest(Reservation reservation, UserDetails currentUser) {
        return reservation.getGuest() != null && reservation.getGuest().getEmail().equals(currentUser.getUsername());
    }
}
