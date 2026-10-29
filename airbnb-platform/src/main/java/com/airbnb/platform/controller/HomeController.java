package com.airbnb.platform.controller;

import com.airbnb.platform.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final PropertyService propertyService;

    @GetMapping("/")
    public String home(Model model) {
        log.info("Accesez pagina de acasa");
        Pageable pageable = PageRequest.of(0, 6);
        var page = propertyService.findAvailable(pageable);

        model.addAttribute("properties", page.getContent());
        model.addAttribute("totalProperties", page.getTotalElements());
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
