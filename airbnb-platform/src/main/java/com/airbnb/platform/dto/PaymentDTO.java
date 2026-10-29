package com.airbnb.platform.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class PaymentDTO {
    private Long id;

    @NotNull(message = "ID-ul rezervarii este obligatoriu")
    private Long reservationId;

    @NotNull(message = "Suma este obligatorie")
    @DecimalMin(value = "0.0", inclusive = false, message = "Suma trebuie sa fie mai mare decat 0")
    private BigDecimal amount;

    @NotNull(message = "Metoda de plata este obligatorie")
    private String paymentMethod;

    private String status;
    private String transactionId;
}
