package com.airbnb.platform.controller;

import com.airbnb.platform.config.CustomUserDetailsService;
import com.airbnb.platform.config.SecurityConfig;
import com.airbnb.platform.dto.UserDTO;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role("GUEST")
                .build();

        testUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("hashedPassword")
                .role("GUEST")
                .enabled(true)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListUsers() throws Exception {
        when(userService.getAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(testUser)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/detail"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowCreateUserForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
        when(userService.convertToEntity(any(UserDTO.class))).thenReturn(testUser);
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .with(csrf())
                .param("firstName", "Test")
                .param("lastName", "User")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("role", "GUEST"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void testAccessDeniedWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }
}
