package com.airbnb.platform.controller;

import com.airbnb.platform.dto.UserDTO;
import com.airbnb.platform.exception.InvalidRequestException;
import com.airbnb.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String role, Model model) {
        UserDTO user = new UserDTO();
        user.setRole("HOST".equals(role) ? "HOST" : "GUEST");
        model.addAttribute("user", user);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDTO userDTO,
                          BindingResult result,
                          @RequestParam(required = false) String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        // Rolul vine din formular, deci nu se permite ADMIN
        userDTO.setRole("HOST".equals(userDTO.getRole()) ? "HOST" : "GUEST");

        String password = userDTO.getPassword();
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            result.rejectValue("password", "password.short", "Parola trebuie sa aiba cel putin 8 caractere");
        } else if (!password.equals(confirmPassword)) {
            result.rejectValue("password", "password.mismatch", "Parolele nu coincid");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(userDTO);
            redirectAttributes.addFlashAttribute("success", "Inregistrare reusita. Va rugam sa va autentificati.");
            return "redirect:/auth/login";
        } catch (InvalidRequestException | IllegalArgumentException e) {
            result.rejectValue("email", "email.exists", e.getMessage());
            return "auth/register";
        }
    }
}
