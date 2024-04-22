package com.example.chatservertest.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtTokenProviderTests {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(); // Initialize with your secret or configuration
    }

    @Test
    void testCreateAndValidateToken() {
        String username = "user";
        long validityInMilliseconds = 3600000; // 1 hour
        String token = jwtTokenProvider.createToken(username, validityInMilliseconds);

        // Validate the created token
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testValidateTokenWithInvalidSignature() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        // This token does not use the correct secret key and should fail validation
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }
}
