package com.airbnb.userservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-that-is-long-enough-for-hmac-sha-signing-keys");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60_000L);
    }

    @Test
    void generatedTokenIsValidAndCarriesClaims() {
        String token = jwtUtil.generateToken("guest@example.com", 42L);

        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
        assertEquals("guest@example.com", jwtUtil.extractEmail(token));
        assertEquals(42L, jwtUtil.extractUserId(token));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String token = jwtUtil.generateToken("guest@example.com", 42L);
        JwtUtil other = new JwtUtil();
        ReflectionTestUtils.setField(other, "secret", "a-different-secret-that-is-also-long-enough-for-hmac-keys");
        ReflectionTestUtils.setField(other, "expiration", 60_000L);

        assertFalse(other.validateToken(token));
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1_000L);
        String token = jwtUtil.generateToken("guest@example.com", 42L);

        assertFalse(jwtUtil.validateToken(token));
        assertTrue(jwtUtil.isTokenExpired(token));
    }
}
