package com.airbnb.platform.controller;

import com.airbnb.platform.dto.UserDTO;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public String listUsers(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        log.info("Listez utilizatori cu paginare: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        var users = userService.getAllUsers(pageable);
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        log.info("Vizionez utilizatorul: {}", id);
        var user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/detail";
        }
        return "redirect:/users";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        log.info("Afisez formular pentru creare utilizator");
        model.addAttribute("user", new UserDTO());
        return "user/form";
    }

    @PostMapping
    public String saveUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (userDTO.getPassword() == null || userDTO.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Parola trebuie sa aiba cel putin 8 caractere");
        }
        if (bindingResult.hasErrors()) {
            return "user/form";
        }
        log.info("Salvez utilizator cu email: {}", userDTO.getEmail());
        try {
            var user = userService.convertToEntity(userDTO);
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("message", "Utilizator creat cu succes");
            return "redirect:/users";
        } catch (Exception e) {
            log.error("Eroare la crearea utilizatorului", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("Afisez formular pentru editare utilizator: {}", id);
        var user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", userService.convertToDTO(user.get()));
            return "user/form";
        }
        return "redirect:/users";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                            @Valid @ModelAttribute("user") UserDTO userDTO,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank() && userDTO.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Parola trebuie sa aiba cel putin 8 caractere");
        }
        if (bindingResult.hasErrors()) {
            return "user/form";
        }
        log.info("Actualizez utilizator: {}", id);
        try {
            userService.updateProfile(id, userDTO);
            redirectAttributes.addFlashAttribute("message", "Utilizator actualizat cu succes");
            return "redirect:/users/" + id;
        } catch (Exception e) {
            log.error("Eroare la actualizarea utilizatorului", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                            RedirectAttributes redirectAttributes) {
        log.info("Sterg utilizator: {}", id);
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Utilizator sters cu succes");
        } catch (Exception e) {
            log.error("Eroare la stergerea utilizatorului", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }
}
