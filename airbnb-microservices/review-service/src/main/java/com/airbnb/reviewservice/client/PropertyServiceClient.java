package com.airbnb.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service")
public interface PropertyServiceClient {
    @GetMapping("/api/properties/{id}")
    PropertyInfo getProperty(@PathVariable Long id);

    class PropertyInfo {
        public Long id;
        public String name;
        public String city;
    }
}
