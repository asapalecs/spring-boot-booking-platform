package com.airbnb.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Emailul trebuie sa fie valid")
    @NotBlank(message = "Emailul este obligatoriu")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    private String password;
}
