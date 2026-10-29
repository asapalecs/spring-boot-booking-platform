package com.airbnb.platform.controller;

import com.airbnb.platform.dto.ReviewDTO;
import com.airbnb.platform.entity.Review;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.ReviewService;
import com.airbnb.platform.service.UserService;
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

@Controller
@RequestMapping("/properties/{propertyId}/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final PropertyService propertyService;
    private final UserService userService;

    @GetMapping
    public String listReviews(@PathVariable Long propertyId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        log.info("Listez recenziile proprietatii: {}", propertyId);
        var property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            Pageable pageable = PageRequest.of(page, size);
            var reviews = reviewService.findByProperty(propertyId, pageable);
            var averageRating = reviewService.getAverageRating(propertyId);

            ReviewDTO newReview = new ReviewDTO();
            newReview.setPropertyId(propertyId);

            model.addAttribute("property", property.get());
            model.addAttribute("reviews", reviews);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("review", newReview);
            return "review/list";
        }
        return "redirect:/properties";
    }

    @GetMapping("/{reviewId}")
    public String viewReview(@PathVariable Long propertyId,
                            @PathVariable Long reviewId,
                            Model model) {
        log.info("Vizionez recenzia: {}", reviewId);
        var review = reviewService.getReviewById(reviewId);
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            model.addAttribute("propertyId", propertyId);
            return "review/detail";
        }
        return "redirect:/properties/" + propertyId + "/reviews";
    }

    @GetMapping("/new")
    public String createForm(@PathVariable Long propertyId, Model model) {
        log.info("Afisez formular pentru creare recenzie la proprietate: {}", propertyId);
        var property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            ReviewDTO review = new ReviewDTO();
            review.setPropertyId(propertyId);
            model.addAttribute("review", review);
            model.addAttribute("property", property.get());
            model.addAttribute("propertyId", propertyId);
            return "review/form";
        }
        return "redirect:/properties";
    }

    @PostMapping
    public String saveReview(@PathVariable Long propertyId,
                            @ModelAttribute("review") ReviewDTO reviewDTO,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails currentUser,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Salvez recenzie pentru proprietate: {}", propertyId);
        reviewDTO.setPropertyId(propertyId);
        userService.getUserByEmail(currentUser.getUsername())
            .ifPresent(user -> reviewDTO.setUserId(user.getId()));
        validateContent(reviewDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            propertyService.getPropertyById(propertyId).ifPresent(p -> model.addAttribute("property", p));
            model.addAttribute("propertyId", propertyId);
            return "review/form";
        }
        try {
            reviewService.createReview(reviewDTO);
            redirectAttributes.addFlashAttribute("message", "Recenzie adaugata cu succes");
            return "redirect:/properties/" + propertyId + "/reviews";
        } catch (Exception e) {
            log.error("Eroare la crearea recenziei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + propertyId + "/reviews/new";
        }
    }

    @GetMapping("/{reviewId}/edit")
    public String editForm(@PathVariable Long propertyId,
                          @PathVariable Long reviewId,
                          @AuthenticationPrincipal UserDetails currentUser,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        log.info("Afisez formular pentru editare recenzie: {}", reviewId);
        var review = reviewService.getReviewById(reviewId);
        if (review.isPresent()) {
            if (!canModify(review.get(), currentUser)) {
                redirectAttributes.addFlashAttribute("error", "Poti modifica doar recenziile tale");
                return "redirect:/properties/" + propertyId + "/reviews";
            }
            model.addAttribute("review", reviewService.convertToDTO(review.get()));
            model.addAttribute("property", review.get().getProperty());
            model.addAttribute("propertyId", propertyId);
            return "review/form";
        }
        return "redirect:/properties/" + propertyId + "/reviews";
    }

    @PostMapping("/{reviewId}")
    public String updateReview(@PathVariable Long propertyId,
                              @PathVariable Long reviewId,
                              @ModelAttribute("review") ReviewDTO reviewDTO,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails currentUser,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        log.info("Actualizez recenzie: {}", reviewId);
        var existing = reviewService.getReviewById(reviewId);
        if (existing.isEmpty() || !canModify(existing.get(), currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Poti modifica doar recenziile tale");
            return "redirect:/properties/" + propertyId + "/reviews";
        }
        validateContent(reviewDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("property", existing.get().getProperty());
            model.addAttribute("propertyId", propertyId);
            return "review/form";
        }
        try {
            reviewService.updateReview(reviewId, reviewDTO);
            redirectAttributes.addFlashAttribute("message", "Recenzie actualizata cu succes");
            return "redirect:/properties/" + propertyId + "/reviews";
        } catch (Exception e) {
            log.error("Eroare la actualizarea recenziei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + propertyId + "/reviews/" + reviewId + "/edit";
        }
    }

    @GetMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long propertyId,
                              @PathVariable Long reviewId,
                              @AuthenticationPrincipal UserDetails currentUser,
                              RedirectAttributes redirectAttributes) {
        log.info("Sterg recenzie: {}", reviewId);
        var existing = reviewService.getReviewById(reviewId);
        if (existing.isPresent() && !canModify(existing.get(), currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Poti sterge doar recenziile tale");
            return "redirect:/properties/" + propertyId + "/reviews";
        }
        try {
            reviewService.deleteReview(reviewId);
            redirectAttributes.addFlashAttribute("message", "Recenzie stearsa cu succes");
        } catch (Exception e) {
            log.error("Eroare la stergerea recenziei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/properties/" + propertyId + "/reviews";
    }

    // userId si propertyId sunt setate din context, deci se valideaza doar continutul
    private void validateContent(ReviewDTO reviewDTO, BindingResult bindingResult) {
        Integer rating = reviewDTO.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            bindingResult.rejectValue("rating", "rating.invalid", "Ratingul trebuie sa fie intre 1 si 5");
        }
        if (reviewDTO.getComment() == null || reviewDTO.getComment().isBlank()) {
            bindingResult.rejectValue("comment", "comment.blank", "Comentariul este obligatoriu");
        }
    }

    private boolean canModify(Review review, UserDetails currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return isAdmin || review.getUser().getEmail().equals(currentUser.getUsername());
    }
}
