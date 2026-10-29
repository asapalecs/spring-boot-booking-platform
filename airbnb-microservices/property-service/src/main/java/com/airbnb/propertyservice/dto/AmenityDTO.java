package com.airbnb.propertyservice.dto;

import com.airbnb.propertyservice.entity.Amenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;

    public static AmenityDTO fromEntity(Amenity amenity) {
        return AmenityDTO.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .icon(amenity.getIcon())
                .build();
    }
}
