package com.airbnb.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{id}")
    UserInfo getUser(@PathVariable Long id);

    class UserInfo {
        public Long id;
        public String firstName;
        public String lastName;
    }
}
