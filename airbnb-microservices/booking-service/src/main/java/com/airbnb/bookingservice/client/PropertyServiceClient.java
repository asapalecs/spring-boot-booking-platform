package com.airbnb.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service")
public interface PropertyServiceClient {
    @GetMapping("/api/properties/{id}")
    PropertyInfo getProperty(@PathVariable Long id);

    class PropertyInfo {
        public Long id;
        public Double pricePerNight;
        public String name;
        public Integer maxGuests;
        public Boolean available;
    }
}
