package com.airbnb.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;

    private Long guestId;

    @NotNull(message = "ID-ul proprietatii este obligatoriu")
    private Long propertyId;

    @NotNull(message = "Data de check-in este obligatorie")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @NotNull(message = "Data de check-out este obligatorie")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    private int numberOfGuests = 1;
    private String status;
    private BigDecimal totalPrice;
}
