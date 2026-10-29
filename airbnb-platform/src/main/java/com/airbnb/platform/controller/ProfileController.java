package com.airbnb.platform.controller;

import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.ReservationService;
import com.airbnb.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final UserService userService;
    private final PropertyService propertyService;
    private final ReservationService reservationService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        log.info("Accesez profilul pentru: {}", currentUser.getUsername());
        return userService.getUserByEmail(currentUser.getUsername())
            .map(user -> {
                model.addAttribute("user", user);
                var reservations = reservationService.findByGuest(user.getId(), PageRequest.of(0, 10));
                model.addAttribute("userReservations", reservations.getContent());
                var properties = propertyService.findByHost(user.getId(), PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id")));
                model.addAttribute("userProperties", properties.getContent());
                return "user/detail";
            })
            .orElse("redirect:/");
    }

    @GetMapping("/my-properties")
    public String myProperties(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        log.info("Listez proprietatile pentru: {}", currentUser.getUsername());
        return userService.getUserByEmail(currentUser.getUsername())
            .map(user -> {
                var properties = propertyService.findByHost(
                    user.getId(),
                    PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"))
                );
                model.addAttribute("properties", properties);
                return "property/list";
            })
            .orElse("redirect:/");
    }
}
