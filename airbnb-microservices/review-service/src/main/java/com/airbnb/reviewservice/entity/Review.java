package com.airbnb.reviewservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long userId;

    @Column(nullable = false)
    @NotNull
    private Long propertyId;

    @Column(nullable = false)
    @NotNull
    @Min(value = 1, message = "Ratingul trebuie sa fie cel putin 1")
    @Max(value = 5, message = "Ratingul nu poate depasi 5")
    private Integer rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
