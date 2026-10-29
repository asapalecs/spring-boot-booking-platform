package com.airbnb.platform.controller;

import com.airbnb.platform.dto.RoomDTO;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/properties/{propertyId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    private final RoomService roomService;
    private final PropertyService propertyService;

    @GetMapping
    public String listRooms(@PathVariable Long propertyId, Model model) {
        log.info("Listez camerele proprietatii: {}", propertyId);
        var property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            var rooms = roomService.findByProperty(propertyId);
            model.addAttribute("property", property.get());
            model.addAttribute("rooms", rooms);
            return "room/list";
        }
        return "redirect:/properties";
    }

    @GetMapping("/{roomId}")
    public String viewRoom(@PathVariable Long propertyId,
                          @PathVariable Long roomId,
                          Model model) {
        log.info("Vizionez camera: {}", roomId);
        var room = roomService.getRoomById(roomId);
        if (room.isPresent()) {
            model.addAttribute("room", room.get());
            model.addAttribute("propertyId", propertyId);
            return "room/detail";
        }
        return "redirect:/properties/" + propertyId + "/rooms";
    }

    @GetMapping("/new")
    public String createForm(@PathVariable Long propertyId, Model model) {
        log.info("Afisez formular pentru creare camera la proprietate: {}", propertyId);
        var property = propertyService.getPropertyById(propertyId);
        if (property.isPresent()) {
            RoomDTO room = new RoomDTO();
            room.setPropertyId(propertyId);
            room.setAvailable(true);
            model.addAttribute("room", room);
            model.addAttribute("propertyId", propertyId);
            return "room/form";
        }
        return "redirect:/properties";
    }

    @PostMapping
    public String saveRoom(@PathVariable Long propertyId,
                          @Valid @ModelAttribute("room") RoomDTO roomDTO,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        log.info("Salvez camera pentru proprietate: {}", propertyId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyId", propertyId);
            return "room/form";
        }
        try {
            roomDTO.setPropertyId(propertyId);
            var room = roomService.convertToEntity(roomDTO);
            var property = propertyService.getPropertyById(propertyId).orElse(null);
            room.setProperty(property);
            roomService.createRoom(room);
            redirectAttributes.addFlashAttribute("message", "Camera creata cu succes");
            return "redirect:/properties/" + propertyId + "/rooms";
        } catch (Exception e) {
            log.error("Eroare la crearea camerei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + propertyId + "/rooms/new";
        }
    }

    @GetMapping("/{roomId}/edit")
    public String editForm(@PathVariable Long propertyId,
                          @PathVariable Long roomId,
                          Model model) {
        log.info("Afisez formular pentru editare camera: {}", roomId);
        var room = roomService.getRoomById(roomId);
        if (room.isPresent()) {
            model.addAttribute("room", roomService.convertToDTO(room.get()));
            model.addAttribute("propertyId", propertyId);
            return "room/form";
        }
        return "redirect:/properties/" + propertyId + "/rooms";
    }

    @PostMapping("/{roomId}")
    public String updateRoom(@PathVariable Long propertyId,
                            @PathVariable Long roomId,
                            @Valid @ModelAttribute("room") RoomDTO roomDTO,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Actualizez camera: {}", roomId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertyId", propertyId);
            return "room/form";
        }
        try {
            roomService.updateRoom(roomId, roomDTO);
            redirectAttributes.addFlashAttribute("message", "Camera actualizata cu succes");
            return "redirect:/properties/" + propertyId + "/rooms";
        } catch (Exception e) {
            log.error("Eroare la actualizarea camerei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + propertyId + "/rooms/" + roomId + "/edit";
        }
    }

    @GetMapping("/{roomId}/delete")
    public String deleteRoom(@PathVariable Long propertyId,
                            @PathVariable Long roomId,
                            RedirectAttributes redirectAttributes) {
        log.info("Sterg camera: {}", roomId);
        try {
            roomService.deleteRoom(roomId);
            redirectAttributes.addFlashAttribute("message", "Camera stearsa cu succes");
        } catch (Exception e) {
            log.error("Eroare la stergerea camerei", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/properties/" + propertyId + "/rooms";
    }
}
