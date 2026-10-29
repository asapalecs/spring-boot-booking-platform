package com.airbnb.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {
    private Long id;

    private Long hostId;

    @NotBlank(message = "Numele proprietatii este obligatoriu")
    private String name;

    @NotBlank(message = "Descrierea este obligatorie")
    private String description;

    private String propertyType;

    private String address;

    @NotBlank(message = "Orasul este obligatoriu")
    private String city;

    @NotBlank(message = "Tara este obligatorie")
    private String country;

    private int maxGuests;

    @NotNull(message = "Pretul pe noapte este obligatoriu")
    @DecimalMin(value = "0.0", inclusive = false, message = "Pretul trebuie sa fie mai mare decat 0")
    private BigDecimal pricePerNight;

    private Boolean available;
    private List<RoomDTO> rooms;
    private Set<AmenityDTO> amenities;
    private Set<Long> amenityIds;
}
