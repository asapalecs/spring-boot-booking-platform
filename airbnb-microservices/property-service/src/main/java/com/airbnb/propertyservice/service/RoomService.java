package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.RoomDTO;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    RoomDTO createRoom(RoomDTO roomDTO);
    Optional<RoomDTO> getRoomById(Long id);
    List<RoomDTO> getRoomsByProperty(Long propertyId);
    RoomDTO updateRoom(Long id, RoomDTO roomDTO);
    void deleteRoom(Long id);
}
