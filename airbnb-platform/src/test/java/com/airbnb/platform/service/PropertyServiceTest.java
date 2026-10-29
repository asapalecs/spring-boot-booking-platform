package com.airbnb.platform.service;

import com.airbnb.platform.dto.PropertyDTO;
import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.AmenityRepository;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.impl.PropertyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {
    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private Property testProperty;
    private PropertyDTO testPropertyDTO;
    private User testHost;

    @BeforeEach
    void setUp() {
        testHost = User.builder()
            .id(1L)
            .email("host@test.com")
            .password("password123")
            .firstName("Host")
            .lastName("User")
            .role("HOST")
            .enabled(true)
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

        testPropertyDTO = PropertyDTO.builder()
            .hostId(1L)
            .name("Cozy Apartment")
            .description("A beautiful apartment")
            .city("New York")
            .country("USA")
            .pricePerNight(new BigDecimal("150.00"))
            .build();
    }

    @Test
    void testGetAllProperties() {
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> page = new PageImpl<>(properties);
        when(propertyRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Property> result = propertyService.getAllProperties(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Cozy Apartment", result.getContent().get(0).getName());
        verify(propertyRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllPropertiesEmpty() {
        Page<Property> page = new PageImpl<>(Arrays.asList());
        when(propertyRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Property> result = propertyService.getAllProperties(PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(propertyRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetPropertyById() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));

        Optional<Property> result = propertyService.getPropertyById(1L);

        assertTrue(result.isPresent());
        assertEquals("Cozy Apartment", result.get().getName());
        assertEquals(1L, result.get().getId());
        verify(propertyRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPropertyByIdNotFound() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Property> result = propertyService.getPropertyById(999L);

        assertFalse(result.isPresent());
        verify(propertyRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateProperty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        Property result = propertyService.createProperty(testProperty);

        assertNotNull(result);
        assertEquals("Cozy Apartment", result.getName());
        assertEquals(testHost, result.getHost());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdateProperty() {
        testPropertyDTO.setName("Updated Apartment");
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        Property result = propertyService.updateProperty(1L, testPropertyDTO);

        assertNotNull(result);
        verify(propertyRepository, times(1)).findById(1L);
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testUpdatePropertyNotFound() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> propertyService.updateProperty(999L, testPropertyDTO));
        verify(propertyRepository, times(1)).findById(999L);
    }

    @Test
    void testDeleteProperty() {
        when(propertyRepository.existsById(1L)).thenReturn(true);

        propertyService.deleteProperty(1L);

        verify(propertyRepository, times(1)).existsById(1L);
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePropertyNotFound() {
        when(propertyRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> propertyService.deleteProperty(999L));
        verify(propertyRepository, times(1)).existsById(999L);
    }

    @Test
    void testGetPropertiesByCity() {
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> page = new PageImpl<>(properties);
        when(propertyRepository.findByCity(eq("New York"), any(Pageable.class))).thenReturn(page);

        Page<Property> result = propertyService.findByCity("New York", PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("New York", result.getContent().get(0).getCity());
        verify(propertyRepository, times(1)).findByCity(eq("New York"), any(Pageable.class));
    }

    @Test
    void testGetAvailableProperties() {
        List<Property> properties = Arrays.asList(testProperty);
        Page<Property> page = new PageImpl<>(properties);
        when(propertyRepository.findByAvailableTrue(any(Pageable.class))).thenReturn(page);

        Page<Property> result = propertyService.findAvailable(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getAvailable());
        verify(propertyRepository, times(1)).findByAvailableTrue(any(Pageable.class));
    }
}
