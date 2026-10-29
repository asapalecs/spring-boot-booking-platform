package com.airbnb.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method")
    @Builder.Default
    private String paymentMethod = "CREDIT_CARD";

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Reservation reservation;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
