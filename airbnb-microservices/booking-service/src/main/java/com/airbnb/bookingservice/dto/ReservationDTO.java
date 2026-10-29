package com.airbnb.bookingservice.dto;

import com.airbnb.bookingservice.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private Long guestId;
    private Long propertyId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfGuests;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;

    public static ReservationDTO fromEntity(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .guestId(reservation.getGuestId())
                .propertyId(reservation.getPropertyId())
                .checkIn(reservation.getCheckIn())
                .checkOut(reservation.getCheckOut())
                .numberOfGuests(reservation.getNumberOfGuests())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
