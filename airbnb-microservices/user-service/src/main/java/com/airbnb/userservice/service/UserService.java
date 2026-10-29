package com.airbnb.userservice.service;

import com.airbnb.userservice.dto.LoginRequest;
import com.airbnb.userservice.dto.LoginResponse;
import com.airbnb.userservice.dto.RegisterRequest;
import com.airbnb.userservice.dto.UserDTO;
import com.airbnb.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO register(RegisterRequest request);
    LoginResponse authenticate(LoginRequest request);
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    Optional<User> getUserEntityById(Long id);
}
