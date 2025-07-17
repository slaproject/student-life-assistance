package com.studentapp.backend.security;

import com.studentapp.common.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for JwtUtil, covering JWT creation and validation logic.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "testsecretkeytestsecretkeytestsecretkey";
    private final long expiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        Field secretField = JwtUtil.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, secret);
        Field expField = JwtUtil.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.set(jwtUtil, expiration);
    }

    /**
     * Verifies generateToken creates a valid JWT with correct claims.
     */
    @Test
    void generateTokenCreatesValidJwt() {
        User user = new User();
        user.setUsername("testuser");
        user.setId(UUID.randomUUID());
        String token = jwtUtil.generateToken(user);
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.extractUserIdFromToken(token)).isEqualTo(user.getId());
    }

    /**
     * Verifies validateToken returns false for invalid token.
     */
    @Test
    void validateTokenInvalidReturnsFalse() {
        String invalidToken = "invalid.token.value";
        assertThat(jwtUtil.validateToken(invalidToken)).isFalse();
    }

    /**
     * Verifies extractUsername throws for invalid token.
     */
    @Test
    void extractUsernameInvalidTokenThrows() {
        String invalidToken = "invalid.token.value";
        assertThatThrownBy(() -> jwtUtil.extractUsername(invalidToken)).isInstanceOf(Exception.class);
    }

    /**
     * Verifies extractUserIdFromToken throws for invalid token.
     */
    @Test
    void extractUserIdFromTokenInvalidTokenThrows() {
        String invalidToken = "invalid.token.value";
        assertThatThrownBy(() -> jwtUtil.extractUserIdFromToken(invalidToken)).isInstanceOf(Exception.class);
    }
} 