package com.airbnb.platform.integration;

import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PropertyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    private User testHost;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        testHost = User.builder()
            .email("host@test.com")
            .password("$2a$10$slYQmyNdGzin7olVN3p5a.32LHvQkCO6UeeCnstw5p2EKMUvzoPmy")
            .firstName("Host")
            .lastName("User")
            .phoneNumber("1234567890")
            .role("HOST")
            .enabled(true)
            .build();
        userRepository.save(testHost);

        testProperty = Property.builder()
            .name("Cozy Apartment")
            .description("A beautiful apartment in the city")
            .address("123 Main St")
            .city("New York")
            .country("USA")
            .pricePerNight(new BigDecimal("150.00"))
            .maxGuests(4)
            .available(true)
            .host(testHost)
            .build();
        propertyRepository.save(testProperty);
    }

    @Test
    void testListPropertiesIntegration() throws Exception {
        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk())
            .andExpect(view().name("property/list"))
            .andExpect(model().attributeExists("properties"));
    }

    @Test
    void testGetPropertyDetailIntegration() throws Exception {
        mockMvc.perform(get("/properties/" + testProperty.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("property/detail"))
            .andExpect(model().attributeExists("property"));
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testUpdatePropertyIntegration() throws Exception {
        mockMvc.perform(post("/properties/" + testProperty.getId())
            .with(csrf())
            .param("hostId", String.valueOf(testHost.getId()))
            .param("name", "Updated Apartment")
            .param("description", "An updated beautiful apartment")
            .param("city", "New York")
            .param("country", "USA")
            .param("pricePerNight", "175.00"))
            .andExpect(status().is3xxRedirection());

        Property updated = propertyRepository.findById(testProperty.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals("Updated Apartment", updated.getName());
        assertEquals(new BigDecimal("175.00"), updated.getPricePerNight());
    }

    @Test
    @WithMockUser(roles = "HOST")
    void testDeletePropertyIntegration() throws Exception {
        mockMvc.perform(get("/properties/" + testProperty.getId() + "/delete")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());

        assertFalse(propertyRepository.existsById(testProperty.getId()));
    }

    @Test
    void testSearchPropertiesByKeyword() throws Exception {
        mockMvc.perform(get("/properties/search")
            .param("keyword", "Cozy"))
            .andExpect(status().isOk());
    }

    @Test
    void testPropertiesAvailableFiltering() throws Exception {
        testProperty.setAvailable(false);
        propertyRepository.save(testProperty);

        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk());
    }

    @Test
    void testMultiplePropertiesIntegration() throws Exception {
        User secondHost = User.builder()
            .email("host2@test.com")
            .password("password123")
            .firstName("Host2")
            .lastName("User2")
            .phoneNumber("9876543210")
            .role("HOST")
            .enabled(true)
            .build();
        userRepository.save(secondHost);

        Property secondProperty = Property.builder()
            .name("Beautiful House")
            .description("A beautiful house")
            .address("789 Oak St")
            .city("Los Angeles")
            .country("USA")
            .pricePerNight(new BigDecimal("200.00"))
            .maxGuests(5)
            .available(true)
            .host(secondHost)
            .build();
        propertyRepository.save(secondProperty);

        mockMvc.perform(get("/properties"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("properties"));
    }
}
