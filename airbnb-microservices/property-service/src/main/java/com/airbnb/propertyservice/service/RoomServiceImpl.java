package com.airbnb.propertyservice.service;

import com.airbnb.propertyservice.dto.RoomDTO;
import com.airbnb.propertyservice.entity.Property;
import com.airbnb.propertyservice.entity.Room;
import com.airbnb.propertyservice.repository.PropertyRepository;
import com.airbnb.propertyservice.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Property property = propertyRepository.findById(roomDTO.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Proprietatea nu a fost gasita"));

        Room room = Room.builder()
                .name(roomDTO.getName())
                .roomType(roomDTO.getRoomType())
                .capacity(roomDTO.getCapacity())
                .pricePerNight(roomDTO.getPricePerNight())
                .available(roomDTO.getAvailable())
                .property(property)
                .build();

        Room saved = roomRepository.save(room);
        return RoomDTO.fromEntity(saved);
    }

    @Override
    public Optional<RoomDTO> getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(RoomDTO::fromEntity);
    }

    @Override
    public List<RoomDTO> getRoomsByProperty(Long propertyId) {
        return roomRepository.findByPropertyId(propertyId).stream()
                .map(RoomDTO::fromEntity)
                .toList();
    }

    @Override
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Camera nu a fost gasita"));

        if (roomDTO.getName() != null) {
            room.setName(roomDTO.getName());
        }
        if (roomDTO.getPricePerNight() != null) {
            room.setPricePerNight(roomDTO.getPricePerNight());
        }
        if (roomDTO.getAvailable() != null) {
            room.setAvailable(roomDTO.getAvailable());
        }

        Room updated = roomRepository.save(room);
        return RoomDTO.fromEntity(updated);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
