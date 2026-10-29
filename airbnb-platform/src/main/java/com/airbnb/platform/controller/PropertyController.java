package com.airbnb.platform.controller;

import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.service.AmenityService;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.RoomService;
import com.airbnb.platform.service.ReviewService;
import com.airbnb.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyController {
    private final PropertyService propertyService;
    private final RoomService roomService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final AmenityService amenityService;

    @GetMapping
    public String listProperties(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String sort,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice,
                                Model model) {
        log.info("Listez proprietati cu filtre: city={}, minPrice={}, maxPrice={}, sort={}", city, minPrice, maxPrice, sort);

        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        Page<com.airbnb.platform.entity.Property> properties;
        if (city != null && !city.isBlank()) {
            properties = propertyService.findByCity(city, pageable);
        } else if (minPrice != null && maxPrice != null) {
            properties = propertyService.findByPriceRange(minPrice, maxPrice, pageable);
        } else {
            properties = propertyService.getAllProperties(pageable);
        }

        model.addAttribute("properties", properties);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentCity", city);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        return "property/list";
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isEmpty()) return Sort.by(Sort.Direction.DESC, "id");
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "pricePerNight");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "pricePerNight");
            case "name" -> Sort.by(Sort.Direction.ASC, "name");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };
    }

    @GetMapping("/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        log.info("Vizionez proprietate: {}", id);
        var property = propertyService.getPropertyById(id);
        if (property.isPresent()) {
            var rooms = roomService.findByProperty(id);
            var reviews = reviewService.findByProperty(id, PageRequest.of(0, 10));
            var averageRating = reviewService.getAverageRating(id);

            model.addAttribute("property", property.get());
            model.addAttribute("rooms", rooms);
            model.addAttribute("reviews", reviews.getContent());
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("host", property.get().getHost());
            model.addAttribute("amenities", property.get().getAmenities());
            return "property/detail";
        }
        return "redirect:/properties";
    }

    @GetMapping("/new")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        log.info("Afisez formular pentru creare proprietate");
        PropertyDTO dto = new PropertyDTO();
        userService.getUserByEmail(currentUser.getUsername())
            .ifPresent(user -> dto.setHostId(user.getId()));
        model.addAttribute("property", dto);
        model.addAttribute("availableAmenities", amenityService.getAllAmenities());
        return "property/form";
    }

    @PostMapping
    public String saveProperty(@Valid @ModelAttribute("property") PropertyDTO propertyDTO,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        boolean isAdmin = currentUser.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin || propertyDTO.getHostId() == null) {
            userService.getUserByEmail(currentUser.getUsername())
                .ifPresent(user -> propertyDTO.setHostId(user.getId()));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableAmenities", amenityService.getAllAmenities());
            return "property/form";
        }
        log.info("Salvez proprietate: {}", propertyDTO.getName());
        try {
            var property = propertyService.convertToEntity(propertyDTO);
            propertyService.createProperty(property);
            redirectAttributes.addFlashAttribute("message", "Proprietate creata cu succes");
            return "redirect:/properties";
        } catch (Exception e) {
            log.error("Eroare la crearea proprietatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("Afisez formular pentru editare proprietate: {}", id);
        var property = propertyService.getPropertyById(id);
        if (property.isPresent()) {
            var dto = propertyService.convertToDTO(property.get());
            dto.setAmenityIds(property.get().getAmenities().stream().map(a -> a.getId()).collect(Collectors.toSet()));
            model.addAttribute("property", dto);
            model.addAttribute("availableAmenities", amenityService.getAllAmenities());
            return "property/form";
        }
        return "redirect:/properties";
    }

    @PostMapping("/{id}")
    public String updateProperty(@PathVariable Long id,
                                @Valid @ModelAttribute("property") PropertyDTO propertyDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableAmenities", amenityService.getAllAmenities());
            return "property/form";
        }
        log.info("Actualizez proprietate: {}", id);
        try {
            propertyService.updateProperty(id, propertyDTO);
            redirectAttributes.addFlashAttribute("message", "Proprietate actualizata cu succes");
            return "redirect:/properties/" + id;
        } catch (Exception e) {
            log.error("Eroare la actualizarea proprietatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + id + "/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteProperty(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        log.info("Sterg proprietate: {}", id);
        try {
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("message", "Proprietate stearsa cu succes");
        } catch (Exception e) {
            log.error("Eroare la stergerea proprietatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/properties";
    }

    @GetMapping("/search")
    public String searchProperties(@RequestParam String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        log.info("Caut proprietati dupa cuvant cheie: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);
        var results = propertyService.searchProperties(keyword, pageable);

        model.addAttribute("properties", results);
        model.addAttribute("keyword", keyword);
        return "property/search-results";
    }
}
