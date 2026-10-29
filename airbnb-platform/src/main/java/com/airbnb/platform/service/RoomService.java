package com.airbnb.platform.service;

import com.airbnb.platform.dto.RoomDTO;
import com.airbnb.platform.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room createRoom(Room room);

    Optional<Room> getRoomById(Long id);

    List<Room> findByProperty(Long propertyId);

    List<Room> findAvailableByProperty(Long propertyId);

    List<Room> getAllRooms();

    Room updateRoom(Long id, RoomDTO roomDTO);

    void deleteRoom(Long id);

    RoomDTO convertToDTO(Room room);

    Room convertToEntity(RoomDTO roomDTO);
}
