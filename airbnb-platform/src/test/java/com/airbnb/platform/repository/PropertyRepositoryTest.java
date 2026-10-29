package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PropertyRepositoryTest {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testHost;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        testHost = User.builder()
            .email("host@test.com")
            .password("password123")
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
        entityManager.flush();
    }

    @Test
    void testFindByCity() {
        List<Property> properties = propertyRepository.findByCity("New York");
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertEquals("New York", properties.get(0).getCity());
    }

    @Test
    void testFindByCity_NoResults() {
        List<Property> properties = propertyRepository.findByCity("Los Angeles");
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testFindByHostId() {
        List<Property> properties = propertyRepository.findByHostId(testHost.getId());
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertEquals(testHost.getId(), properties.get(0).getHost().getId());
    }

    @Test
    void testFindByAvailableTrue() {
        List<Property> properties = propertyRepository.findByAvailableTrue();
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertTrue(properties.get(0).getAvailable());
    }

    @Test
    void testFindByAvailableTrueWhenNoneAvailable() {
        testProperty.setAvailable(false);
        propertyRepository.save(testProperty);
        entityManager.flush();

        List<Property> properties = propertyRepository.findByAvailableTrue();
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testFindByMaxGuestsGreaterThanEqual() {
        List<Property> properties = propertyRepository.findByMaxGuestsGreaterThanEqual(3);
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertTrue(properties.get(0).getMaxGuests() >= 3);
    }

    @Test
    void testFindByPricePerNightBetween() {
        List<Property> properties = propertyRepository.findByPricePerNightBetween(
            new BigDecimal("100.00"),
            new BigDecimal("200.00")
        );
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertTrue(properties.get(0).getPricePerNight().compareTo(new BigDecimal("100.00")) >= 0);
    }
}
