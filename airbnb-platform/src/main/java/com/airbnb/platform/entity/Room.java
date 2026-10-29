package com.airbnb.platform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "room_type")
    @Builder.Default
    private String roomType = "SINGLE";

    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 1;

    @Column(name = "price")
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Property property;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
