package com.airbnb.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Prenumele este obligatoriu")
    private String firstName;

    @NotBlank(message = "Numele este obligatoriu")
    private String lastName;

    @Email(message = "Email trebuie sa fie valid")
    @NotBlank(message = "Email este obligatoriu")
    private String email;

    private String phoneNumber;

    @Builder.Default
    private String role = "GUEST";

    private boolean enabled;

    private String password;
}
