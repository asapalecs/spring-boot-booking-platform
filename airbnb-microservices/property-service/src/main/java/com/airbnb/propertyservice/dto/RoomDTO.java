package com.airbnb.propertyservice.dto;

import com.airbnb.propertyservice.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private String name;
    private String roomType;
    private Integer capacity;
    private Double pricePerNight;
    private Boolean available;
    private Long propertyId;

    public static RoomDTO fromEntity(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .available(room.getAvailable())
                .propertyId(room.getProperty().getId())
                .build();
    }
}
