package com.airbnb.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Prenumele este obligatoriu")
    private String firstName;

    @NotBlank(message = "Numele este obligatoriu")
    private String lastName;

    @Email(message = "Emailul trebuie sa fie valid")
    @NotBlank(message = "Emailul este obligatoriu")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    @Size(min = 6, message = "Parola trebuie sa aiba cel putin 6 caractere")
    private String password;

    @NotBlank(message = "Telefonul este obligatoriu")
    private String phone;

    private String role;
}
