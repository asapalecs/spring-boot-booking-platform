package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.Reservation;
import com.airbnb.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testGuest;
    private User testHost;
    private Property testProperty;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testGuest = User.builder()
            .email("guest@test.com")
            .password("password123")
            .firstName("Guest")
            .lastName("User")
            .phoneNumber("1234567890")
            .role("GUEST")
            .enabled(true)
            .build();
        userRepository.save(testGuest);

        testHost = User.builder()
            .email("host@test.com")
            .password("password123")
            .firstName("Host")
            .lastName("User")
            .phoneNumber("0987654321")
            .role("HOST")
            .enabled(true)
            .build();
        userRepository.save(testHost);

        testProperty = Property.builder()
            .name("Test Property")
            .description("A test property")
            .address("123 Test St")
            .city("Test City")
            .country("USA")
            .pricePerNight(new BigDecimal("100.00"))
            .maxGuests(2)
            .available(true)
            .host(testHost)
            .build();
        propertyRepository.save(testProperty);

        testReservation = Reservation.builder()
            .property(testProperty)
            .guest(testGuest)
            .checkInDate(LocalDate.of(2024, 6, 1))
            .checkOutDate(LocalDate.of(2024, 6, 5))
            .totalPrice(new BigDecimal("400.00"))
            .status("CONFIRMED")
            .build();
        reservationRepository.save(testReservation);
        entityManager.flush();
    }

    @Test
    void testOverlappingStayIsDetected() {
        List<Reservation> conflicts = reservationRepository.findOverlappingReservations(
            testProperty.getId(), LocalDate.of(2024, 6, 3), LocalDate.of(2024, 6, 7));
        assertEquals(1, conflicts.size());
    }

    @Test
    void testBackToBackStayIsAllowed() {
        List<Reservation> conflicts = reservationRepository.findOverlappingReservations(
            testProperty.getId(), LocalDate.of(2024, 6, 5), LocalDate.of(2024, 6, 8));
        assertTrue(conflicts.isEmpty());
    }

    @Test
    void testFindByGuestId() {
        List<Reservation> reservations = reservationRepository.findByGuestId(testGuest.getId());
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(testGuest.getId(), reservations.get(0).getGuest().getId());
    }

    @Test
    void testFindByGuestIdNoResults() {
        User otherGuest = User.builder()
            .email("other@test.com")
            .password("password123")
            .firstName("Other")
            .lastName("Guest")
            .phoneNumber("9999999999")
            .role("GUEST")
            .enabled(true)
            .build();
        userRepository.save(otherGuest);
        entityManager.flush();

        List<Reservation> reservations = reservationRepository.findByGuestId(otherGuest.getId());
        assertNotNull(reservations);
        assertTrue(reservations.isEmpty());
    }

    @Test
    void testFindByStatus() {
        List<Reservation> reservations = reservationRepository.findByStatus("CONFIRMED");
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals("CONFIRMED", reservations.get(0).getStatus());
    }

    @Test
    void testFindByStatusNotFound() {
        List<Reservation> reservations = reservationRepository.findByStatus("CANCELLED");
        assertNotNull(reservations);
        assertTrue(reservations.isEmpty());
    }

    @Test
    void testFindByPropertyId() {
        List<Reservation> reservations = reservationRepository.findByPropertyId(testProperty.getId());
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(testProperty.getId(), reservations.get(0).getProperty().getId());
    }

    @Test
    void testMultipleReservationsForSameProperty() {
        User secondGuest = User.builder()
            .email("guest2@test.com")
            .password("password123")
            .firstName("Second")
            .lastName("Guest")
            .phoneNumber("5555555555")
            .role("GUEST")
            .enabled(true)
            .build();
        userRepository.save(secondGuest);

        Reservation secondReservation = Reservation.builder()
            .property(testProperty)
            .guest(secondGuest)
            .checkInDate(LocalDate.of(2024, 7, 1))
            .checkOutDate(LocalDate.of(2024, 7, 3))
            .totalPrice(new BigDecimal("200.00"))
            .status("PENDING")
            .build();
        reservationRepository.save(secondReservation);
        entityManager.flush();

        List<Reservation> reservations = reservationRepository.findByPropertyId(testProperty.getId());
        assertEquals(2, reservations.size());
    }
}
