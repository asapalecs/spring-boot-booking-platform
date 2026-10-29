package com.airbnb.platform.repository;

import com.airbnb.platform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("john@example.com")
            .password("hashedPassword123")
            .firstName("John")
            .lastName("Doe")
            .phoneNumber("5551234567")
            .role("GUEST")
            .enabled(true)
            .build();
        userRepository.save(testUser);
        entityManager.flush();
    }

    @Test
    void testFindByEmail() {
        Optional<User> user = userRepository.findByEmail("john@example.com");
        assertTrue(user.isPresent());
        assertEquals("john@example.com", user.get().getEmail());
        assertEquals("John", user.get().getFirstName());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> user = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(user.isPresent());
    }

    @Test
    void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("john@example.com");
        assertTrue(exists);
    }

    @Test
    void testExistsByEmailNotFound() {
        boolean exists = userRepository.existsByEmail("notfound@example.com");
        assertFalse(exists);
    }

    @Test
    void testFindByRole() {
        Optional<User> user = userRepository.findByEmail("john@example.com");
        assertTrue(user.isPresent());
        assertEquals("GUEST", user.get().getRole());
    }

    @Test
    void testMultipleUsersWithDifferentEmails() {
        User secondUser = User.builder()
            .email("jane@example.com")
            .password("hashedPassword456")
            .firstName("Jane")
            .lastName("Smith")
            .phoneNumber("5559876543")
            .role("HOST")
            .enabled(true)
            .build();
        userRepository.save(secondUser);
        entityManager.flush();

        Optional<User> foundUser1 = userRepository.findByEmail("john@example.com");
        Optional<User> foundUser2 = userRepository.findByEmail("jane@example.com");

        assertTrue(foundUser1.isPresent());
        assertTrue(foundUser2.isPresent());
        assertNotEquals(foundUser1.get().getId(), foundUser2.get().getId());
    }
}
