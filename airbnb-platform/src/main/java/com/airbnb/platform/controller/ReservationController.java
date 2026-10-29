package com.airbnb.platform.controller;

import com.airbnb.platform.dto.ReservationDTO;
import com.airbnb.platform.entity.Reservation;
import com.airbnb.platform.service.PaymentService;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.ReservationService;
import com.airbnb.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;
    private final PropertyService propertyService;
    private final UserService userService;
    private final PaymentService paymentService;

    @GetMapping
    public String listReservations(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @AuthenticationPrincipal UserDetails currentUser,
                                  Model model) {
        log.info("Listez rezervari cu paginare: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        var reservations = userService.getUserByEmail(currentUser.getUsername())
            .map(user -> reservationService.findByGuest(user.getId(), pageable))
            .orElseGet(() -> reservationService.getAllReservations(pageable));
        model.addAttribute("page", reservations);
        return "reservation/list";
    }

    @GetMapping("/all")
    public String listAllReservations(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        log.info("Listez toate rezervarile (admin)");
        Pageable pageable = PageRequest.of(page, size);
        model.addAttribute("page", reservationService.getAllReservations(pageable));
        return "reservation/list";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails currentUser,
                                  Model model) {
        log.info("Vizionez rezervare: {}", id);
        var reservation = reservationService.getReservationById(id);
        if (reservation.isPresent() && canView(reservation.get(), currentUser)) {
            model.addAttribute("reservation", reservation.get());
            model.addAttribute("payment", paymentService.findByReservation(id).orElse(null));
            return "reservation/detail";
        }
        return "redirect:/reservations";
    }

    @GetMapping("/properties/{propertyId}/reserve")
    public String reservationForm(@PathVariable Long propertyId,
                                  @RequestParam(required = false) LocalDate checkIn,
                                  @RequestParam(required = false) LocalDate checkOut,
                                  @RequestParam(required = false) Integer numberOfGuests,
                                  Model model) {
        log.info("Afisez formular de rezervare pentru proprietate: {}", propertyId);
        var property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            ReservationDTO reservation = new ReservationDTO();
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            if (numberOfGuests != null) {
                reservation.setNumberOfGuests(numberOfGuests);
            }
            model.addAttribute("reservation", reservation);
            model.addAttribute("property", property.get());
            model.addAttribute("propertyId", propertyId);
            return "reservation/form";
        }
        return "redirect:/properties";
    }

    @PostMapping
    public String createReservation(@Valid @ModelAttribute ReservationDTO reservationDTO,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails currentUser,
                                   RedirectAttributes redirectAttributes) {
        userService.getUserByEmail(currentUser.getUsername())
            .ifPresent(user -> reservationDTO.setGuestId(user.getId()));

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/reservations/properties/" + reservationDTO.getPropertyId() + "/reserve";
        }

        log.info("Creez rezervare pentru proprietate: {}", reservationDTO.getPropertyId());
        try {
            var reservation = reservationService.createReservation(reservationDTO);
            redirectAttributes.addFlashAttribute("message", "Rezervare creata cu succes");
            return "redirect:/reservations/" + reservation.getId();
        } catch (Exception e) {
            log.error("Eroare la crearea rezervarii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/properties/" + reservationDTO.getPropertyId() + "/reserve";
        }
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        log.info("Confirm rezervare: {}", id);
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute("message", "Rezervare confirmata");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            log.error("Eroare la confirmarea rezervarii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("Anulez rezervare: {}", id);
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("message", "Rezervare anulata");
            return "redirect:/reservations/" + id;
        } catch (Exception e) {
            log.error("Eroare la anularea rezervarii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/" + id;
        }
    }

    private boolean canView(Reservation reservation, UserDetails currentUser) {
        String email = currentUser.getUsername();
        boolean isAdmin = currentUser.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isGuest = reservation.getGuest() != null && email.equals(reservation.getGuest().getEmail());
        boolean isHost = reservation.getProperty() != null && reservation.getProperty().getHost() != null
            && email.equals(reservation.getProperty().getHost().getEmail());
        return isAdmin || isGuest || isHost;
    }
}
