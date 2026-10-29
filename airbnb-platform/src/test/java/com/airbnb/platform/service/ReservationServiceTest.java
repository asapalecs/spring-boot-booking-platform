package com.airbnb.platform.service;

import com.airbnb.platform.dto.ReservationDTO;
import com.airbnb.platform.entity.Property;
import com.airbnb.platform.entity.Reservation;
import com.airbnb.platform.entity.User;
import com.airbnb.platform.exception.InvalidRequestException;
import com.airbnb.platform.exception.ResourceNotFoundException;
import com.airbnb.platform.repository.PropertyRepository;
import com.airbnb.platform.repository.ReservationRepository;
import com.airbnb.platform.repository.UserRepository;
import com.airbnb.platform.service.impl.ReservationServiceImpl;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation testReservation;
    private ReservationDTO testReservationDTO;
    private User testGuest;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        testGuest = User.builder()
            .id(1L)
            .email("guest@test.com")
            .password("password123")
            .firstName("Guest")
            .lastName("User")
            .phoneNumber("1234567890")
            .role("GUEST")
            .enabled(true)
            .build();

        User testHost = User.builder()
            .id(2L)
            .email("host@test.com")
            .password("password123")
            .firstName("Host")
            .lastName("User")
            .role("HOST")
            .enabled(true)
            .build();

        testProperty = Property.builder()
            .id(1L)
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

        testReservation = Reservation.builder()
            .id(1L)
            .property(testProperty)
            .guest(testGuest)
            .checkInDate(LocalDate.of(2024, 6, 1))
            .checkOutDate(LocalDate.of(2024, 6, 5))
            .totalPrice(new BigDecimal("400.00"))
            .status("CONFIRMED")
            .build();

        testReservationDTO = ReservationDTO.builder()
            .guestId(1L)
            .propertyId(1L)
            .checkInDate(LocalDate.of(2024, 6, 1))
            .checkOutDate(LocalDate.of(2024, 6, 5))
            .build();
    }

    @Test
    void testCreateReservation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testGuest));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(testProperty));
        when(reservationRepository.findOverlappingReservations(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.createReservation(testReservationDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("400.00"), result.getTotalPrice());
        verify(propertyRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCreateReservationPropertyNotFound() {
        ReservationDTO dtoWithMissingProperty = ReservationDTO.builder()
            .guestId(1L)
            .propertyId(999L)
            .checkInDate(LocalDate.of(2024, 6, 1))
            .checkOutDate(LocalDate.of(2024, 6, 5))
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testGuest));
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> reservationService.createReservation(dtoWithMissingProperty));
    }

    @Test
    void testCreateReservationWithCheckOutBeforeCheckIn() {
        ReservationDTO invalidDates = ReservationDTO.builder()
            .guestId(1L)
            .propertyId(1L)
            .checkInDate(LocalDate.of(2024, 6, 5))
            .checkOutDate(LocalDate.of(2024, 6, 1))
            .build();

        assertThrows(InvalidRequestException.class,
            () -> reservationService.createReservation(invalidDates));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testCancelReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        reservationService.cancelReservation(1L);

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        assertEquals("CANCELLED", testReservation.getStatus());
    }

    @Test
    void testCancelReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancelReservation(999L));
    }

    @Test
    void testConfirmReservation() {
        testReservation.setStatus("PENDING");
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        Reservation result = reservationService.confirmReservation(1L);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testGetReservationsByGuestId() {
        List<Reservation> reservations = Arrays.asList(testReservation);
        Page<Reservation> page = new PageImpl<>(reservations);
        when(reservationRepository.findByGuestId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<Reservation> result = reservationService.findByGuest(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testGuest.getId(), result.getContent().get(0).getGuest().getId());
        verify(reservationRepository, times(1)).findByGuestId(eq(1L), any(Pageable.class));
    }

    @Test
    void testGetReservationById() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        Optional<Reservation> result = reservationService.getReservationById(1L);

        assertTrue(result.isPresent());
        assertEquals("CONFIRMED", result.get().getStatus());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void testConfirmReservationNotFound() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.confirmReservation(999L));
    }
}
