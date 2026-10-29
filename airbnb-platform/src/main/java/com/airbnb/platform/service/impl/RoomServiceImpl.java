package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.RoomDTO;
import com.airbnb.platform.entity.Room;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.RoomRepository;
import com.airbnb.platform.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final PropertyRepository propertyRepository;

    @Override
    public Room createRoom(Room room) {
        if (room.getProperty() == null || room.getProperty().getId() == null) {
            log.error("Camera trebuie sa aiba o proprietate asociata");
            throw new IllegalArgumentException("Camera trebuie sa aiba o proprietate asociata");
        }
        log.info("Creez camera pentru proprietate: {}", room.getProperty().getId());
        propertyRepository.findById(room.getProperty().getId())
                .orElseThrow(() -> {
                    log.error("Proprietatea nu a fost gasita");
                    return new ResourceNotFoundException("Proprietatea nu a fost gasita");
                });
        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(Long id) {
        log.debug("Caut camera cu ID: {}", id);
        return roomRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findByProperty(Long propertyId) {
        log.debug("Caut camere pentru proprietate: {}", propertyId);
        return roomRepository.findByPropertyId(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAvailableByProperty(Long propertyId) {
        log.debug("Caut camere disponibile pentru proprietate: {}", propertyId);
        return roomRepository.findByPropertyIdAndAvailableTrue(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        log.debug("Listez toate camerele");
        return roomRepository.findAll();
    }

    @Override
    public Room updateRoom(Long id, RoomDTO roomDTO) {
        log.info("Actualizez camera: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Camera cu ID {} nu a fost gasita", id);
                    return new ResourceNotFoundException("Camera nu a fost gasita cu id: " + id);
                });

        if (roomDTO.getRoomNumber() != null) {
            room.setRoomNumber(roomDTO.getRoomNumber());
        }
        if (roomDTO.getRoomType() != null) {
            room.setRoomType(roomDTO.getRoomType());
        }
        if (roomDTO.getCapacity() != null) {
            room.setCapacity(roomDTO.getCapacity());
        }
        if (roomDTO.getPrice() != null) {
            room.setPrice(roomDTO.getPrice());
        }
        if (roomDTO.getAvailable() != null) {
            room.setAvailable(roomDTO.getAvailable());
        }

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        log.info("Sterg camera: {}", id);
        if (!roomRepository.existsById(id)) {
            log.error("Camera cu ID {} nu a fost gasita", id);
            throw new ResourceNotFoundException("Camera nu a fost gasita cu id: " + id);
        }
        roomRepository.deleteById(id);
    }

    @Override
    public RoomDTO convertToDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .propertyId(room.getProperty().getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .capacity(room.getCapacity())
                .price(room.getPrice())
                .available(room.getAvailable())
                .build();
    }

    @Override
    public Room convertToEntity(RoomDTO roomDTO) {
        Room room = new Room();
        room.setId(roomDTO.getId());
        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setRoomType(roomDTO.getRoomType());
        room.setCapacity(roomDTO.getCapacity());
        room.setPrice(roomDTO.getPrice());
        if (roomDTO.getAvailable() != null) {
            room.setAvailable(roomDTO.getAvailable());
        }
        return room;
    }
}
