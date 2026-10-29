package com.airbnb.propertyservice.controller;

import com.airbnb.propertyservice.dto.RoomDTO;
import com.airbnb.propertyservice.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/{propertyId}/rooms")
    public ResponseEntity<RoomDTO> createRoom(@PathVariable Long propertyId, @Valid @RequestBody RoomDTO roomDTO) {
        roomDTO.setPropertyId(propertyId);
        RoomDTO created = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{propertyId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomsByProperty(@PathVariable Long propertyId) {
        List<RoomDTO> rooms = roomService.getRoomsByProperty(propertyId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long roomId) {
        return roomService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomDTO roomDTO) {
        RoomDTO updated = roomService.updateRoom(roomId, roomDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
