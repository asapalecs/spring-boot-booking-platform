package com.airbnb.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;

    @NotNull(message = "ID-ul proprietatii este obligatoriu")
    private Long propertyId;

    @NotNull(message = "ID-ul utilizatorului este obligatoriu")
    private Long userId;

    @NotNull(message = "Ratingul este obligatoriu")
    @Min(value = 1, message = "Ratingul trebuie sa fie intre 1 si 5")
    @Max(value = 5, message = "Ratingul trebuie sa fie intre 1 si 5")
    private Integer rating;

    @NotBlank(message = "Comentariul este obligatoriu")
    private String comment;
}
