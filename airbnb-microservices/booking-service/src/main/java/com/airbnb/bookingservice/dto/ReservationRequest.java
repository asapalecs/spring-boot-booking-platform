package com.airbnb.bookingservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    @NotNull(message = "ID-ul oaspetelui este obligatoriu")
    private Long guestId;

    @NotNull(message = "ID-ul proprietatii este obligatoriu")
    private Long propertyId;

    @NotNull(message = "Data de check-in este obligatorie")
    private LocalDate checkIn;

    @NotNull(message = "Data de check-out este obligatorie")
    private LocalDate checkOut;

    @NotNull(message = "Numarul de oaspeti este obligatoriu")
    @Positive(message = "Numarul de oaspeti trebuie sa fie pozitiv")
    private Integer numberOfGuests;
}
