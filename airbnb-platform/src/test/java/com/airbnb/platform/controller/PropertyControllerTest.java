package com.airbnb.platform.controller;

import com.airbnb.platform.config.CustomUserDetailsService;
import com.airbnb.platform.config.SecurityConfig;
import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.service.AmenityService;
import com.airbnb.platform.service.PropertyService;
import com.airbnb.platform.service.ReviewService;
import com.airbnb.platform.service.RoomService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
@Import(SecurityConfig.class)
class PropertyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private RoomService roomService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @MockBean
    private AmenityService amenityService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Property testProperty;
    private User testHost;

    @BeforeEach
    void setUp() {
        testHost = User.builder()
            .id(1L)
            .email("host@test.com")
            .firstName("Host")
            .lastName("User")
            .role("HOST")
            .build();

        testProperty = Property.builder()
            .id(1L)
            .name("Cozy Apartment")
            .description("A beautiful apartment")
            .address("123 Main St")
            .city("New York")
            .country("USA")
            .pricePerNight(new BigDecimal("150.00"))
            .maxGuests(4)
            .available(true)
            .host(testHost)
            .build();
    }

    @Test
    @WithMockUser
    void testGetAllProperties() throws Exception {
        List<Property> properties = Arrays.asList(testProperty);
        when(propertyService.getAllProperties(any(Pageable.class)))
            .thenReturn(new PageImpl<>(properties));

        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/list"))
            .andExpect(model().attributeExists("properties"));

        verify(propertyService, times(1)).getAllProperties(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testGetPropertyById() throws Exception {
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(testProperty));
        when(roomService.findByProperty(1L)).thenReturn(Collections.emptyList());
        when(reviewService.findByProperty(eq(1L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(reviewService.getAverageRating(1L)).thenReturn(0.0);

        mockMvc.perform(get("/properties/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/detail"))
            .andExpect(model().attributeExists("property"))
            .andExpect(model().attributeExists("host"))
            .andExpect(model().attributeExists("amenities"));

        verify(propertyService, times(1)).getPropertyById(1L);
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testShowCreatePropertyForm() throws Exception {
        mockMvc.perform(get("/properties/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/form"))
            .andExpect(model().attributeExists("property"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testCreateProperty() throws Exception {
        when(propertyService.convertToEntity(any())).thenReturn(testProperty);
        when(propertyService.createProperty(any(Property.class))).thenReturn(testProperty);

        mockMvc.perform(post("/properties")
            .with(csrf())
            .param("hostId", "1")
            .param("name", "Cozy Apartment")
            .param("description", "A beautiful apartment")
            .param("city", "New York")
            .param("country", "USA")
            .param("pricePerNight", "150.00"))
            .andExpect(status().is3xxRedirection());

        verify(propertyService, times(1)).createProperty(any(Property.class));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testShowEditPropertyForm() throws Exception {
        PropertyDTO testPropertyDTO = PropertyDTO.builder()
            .id(1L)
            .hostId(1L)
            .name("Cozy Apartment")
            .description("A beautiful apartment")
            .city("New York")
            .country("USA")
            .pricePerNight(new BigDecimal("150.00"))
            .build();
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyService.convertToDTO(testProperty)).thenReturn(testPropertyDTO);

        mockMvc.perform(get("/properties/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/form"))
            .andExpect(model().attributeExists("property"));

        verify(propertyService, times(1)).getPropertyById(1L);
    }

    @Test
    @WithMockUser
    void testGetAllPropertiesEmpty() throws Exception {
        when(propertyService.getAllProperties(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/list"));

        verify(propertyService, times(1)).getAllProperties(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testUnauthorizedCreateProperty() throws Exception {
        mockMvc.perform(get("/properties/new"))
            .andExpect(status().isForbidden());
    }
}
