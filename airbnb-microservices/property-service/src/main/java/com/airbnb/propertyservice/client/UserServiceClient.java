package com.airbnb.propertyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    UserInfo getUserInfo(@PathVariable Long id);

    class UserInfo {
        public Long id;
        public String firstName;
        public String lastName;
        public String email;
    }
}
