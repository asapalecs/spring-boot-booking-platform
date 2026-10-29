package com.airbnb.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;

    @NotNull(message = "ID-ul proprietatii este obligatoriu")
    private Long propertyId;

    @NotBlank(message = "Numarul camerei este obligatoriu")
    private String roomNumber;

    @NotBlank(message = "Tipul camerei este obligatoriu")
    private String roomType;

    @NotNull(message = "Capacitatea este obligatorie")
    @Min(value = 1, message = "Capacitatea trebuie sa fie cel putin 1")
    private Integer capacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Pretul trebuie sa fie mai mare decat 0")
    private BigDecimal price;

    private Boolean available;
}
