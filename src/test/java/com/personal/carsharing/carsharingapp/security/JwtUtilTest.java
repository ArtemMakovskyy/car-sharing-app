package com.personal.carsharing.carsharingapp.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {
    private static final String SECRET = "carsharingapp13112023secretkey123456789012";

    @Test
    @DisplayName("Generates valid token and extracts username")
    void generateToken_ValidInput_ReturnsTokenWithUsername() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60_000L);

        String token = jwtUtil.generateToken("user@email.com");

        assertTrue(jwtUtil.isValidToken(token));
        assertEquals("user@email.com", jwtUtil.getUsername(token));
    }

    @Test
    @DisplayName("Throws exception for malformed token")
    void isValidToken_MalformedToken_ThrowsJwtException() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        assertThrows(JwtException.class, () -> jwtUtil.isValidToken("bad-token"));
    }

    @Test
    @DisplayName("Throws exception for expired token")
    void isValidToken_ExpiredToken_ThrowsJwtException() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L);
        String expiredToken = jwtUtil.generateToken("user@email.com");

        assertThrows(JwtException.class, () -> jwtUtil.isValidToken(expiredToken));
    }
}
