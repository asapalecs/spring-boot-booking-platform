package com.airbnb.platform.service;

import com.airbnb.platform.dto.UserDTO;
import com.airbnb.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> findByRole(String role, Pageable pageable);
    User updateProfile(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO convertToDTO(User user);
    User convertToEntity(UserDTO userDTO);

    User registerUser(UserDTO userDTO);
}
