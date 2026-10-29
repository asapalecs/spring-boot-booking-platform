package com.airbnb.propertyservice.dto;

import com.airbnb.propertyservice.entity.Property;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private Double pricePerNight;
    private Integer maxGuests;
    private String propertyType;
    private Boolean available;
    private Long hostId;
    private LocalDateTime createdAt;
    private Set<RoomDTO> rooms;
    private Set<AmenityDTO> amenities;

    public static PropertyDTO fromEntity(Property property) {
        return PropertyDTO.builder()
                .id(property.getId())
                .name(property.getName())
                .description(property.getDescription())
                .address(property.getAddress())
                .city(property.getCity())
                .country(property.getCountry())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .propertyType(property.getPropertyType())
                .available(property.getAvailable())
                .hostId(property.getHostId())
                .createdAt(property.getCreatedAt())
                .build();
    }
}
