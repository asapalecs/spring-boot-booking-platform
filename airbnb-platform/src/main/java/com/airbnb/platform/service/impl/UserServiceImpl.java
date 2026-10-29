package com.airbnb.platform.service.impl;

import com.airbnb.platform.dto.UserDTO;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.exception.InvalidRequestException;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        log.info("Creez utilizator cu email: {}", user.getEmail());
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Emailul {} exista deja", user.getEmail());
            throw new InvalidRequestException("Emailul exista deja");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Caut utilizator cu ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Caut utilizator cu email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Listez toti utilizatorii");
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findByRole(String role, Pageable pageable) {
        log.debug("Caut utilizatori cu rol: {}", role);
        return userRepository.findByRole(role, pageable);
    }

    @Override
    public User updateProfile(Long id, UserDTO userDTO) {
        log.info("Actualizez profilul utilizatorului: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Utilizatorul cu ID {} nu a fost gasit", id);
                    return new ResourceNotFoundException("Utilizatorul nu a fost gasit cu id: " + id);
                });

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                log.warn("Emailul {} exista deja", userDTO.getEmail());
                throw new InvalidRequestException("Emailul exista deja");
            }
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Sterg utilizatorul: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("Utilizatorul cu ID {} nu a fost gasit", id);
            throw new ResourceNotFoundException("Utilizatorul nu a fost gasit cu id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .role(userDTO.getRole() != null ? userDTO.getRole() : "GUEST")
                .password(userDTO.getPassword() != null && !userDTO.getPassword().isBlank() ? passwordEncoder.encode(userDTO.getPassword()) : null)
                .build();
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        log.info("Inregistrez utilizator nou cu email: {}", userDTO.getEmail());
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new InvalidRequestException("Emailul exista deja");
        }
        User user = User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .phoneNumber(userDTO.getPhoneNumber())
                .role(userDTO.getRole() != null ? userDTO.getRole() : "GUEST")
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}
