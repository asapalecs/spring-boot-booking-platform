package com.airbnb.platform.controller;

import com.airbnb.platform.dto.AmenityDTO;
import com.airbnb.platform.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/amenities")
@RequiredArgsConstructor
@Slf4j
public class AmenityController {
    private final AmenityService amenityService;

    @GetMapping
    public String listAmenities(Model model) {
        log.info("Listez toate facilitatile");
        var amenities = amenityService.getAllAmenities();
        model.addAttribute("amenities", amenities);
        return "amenity/list";
    }

    @GetMapping("/{id}")
    public String viewAmenity(@PathVariable Long id, Model model) {
        log.info("Vizionez facilitatea: {}", id);
        var amenity = amenityService.getAmenityById(id);
        if (amenity.isPresent()) {
            model.addAttribute("amenity", amenity.get());
            return "amenity/detail";
        }
        return "redirect:/amenities";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        log.info("Afisez formular pentru creare facilitate");
        model.addAttribute("amenity", new AmenityDTO());
        return "amenity/form";
    }

    @PostMapping
    public String saveAmenity(@Valid @ModelAttribute("amenity") AmenityDTO amenityDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "amenity/form";
        }
        log.info("Salvez facilitate: {}", amenityDTO.getName());
        try {
            amenityService.createAmenity(amenityDTO);
            redirectAttributes.addFlashAttribute("message", "Facilitate creata cu succes");
            return "redirect:/amenities";
        } catch (Exception e) {
            log.error("Eroare la crearea facilitatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/amenities/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("Afisez formular pentru editare facilitate: {}", id);
        var amenity = amenityService.getAmenityById(id);
        if (amenity.isPresent()) {
            model.addAttribute("amenity", amenityService.convertToDTO(amenity.get()));
            return "amenity/form";
        }
        return "redirect:/amenities";
    }

    @PostMapping("/{id}")
    public String updateAmenity(@PathVariable Long id,
                               @Valid @ModelAttribute("amenity") AmenityDTO amenityDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "amenity/form";
        }
        log.info("Actualizez facilitate: {}", id);
        try {
            amenityService.updateAmenity(id, amenityDTO);
            redirectAttributes.addFlashAttribute("message", "Facilitate actualizata cu succes");
            return "redirect:/amenities/" + id;
        } catch (Exception e) {
            log.error("Eroare la actualizarea facilitatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/amenities/" + id + "/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteAmenity(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        log.info("Sterg facilitate: {}", id);
        try {
            amenityService.deleteAmenity(id);
            redirectAttributes.addFlashAttribute("message", "Facilitate stearsa cu succes");
        } catch (Exception e) {
            log.error("Eroare la stergerea facilitatii", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/amenities";
    }
}
