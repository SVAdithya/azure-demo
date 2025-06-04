package com.example.user.persistance.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testDefaultConstructorAndAccessors() {
        User user = new User(); // Test default constructor

        // Test setters and getters for all fields
        user.setId(1L);
        assertEquals(1L, user.getId());

        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());

        user.setUname("testuser");
        assertEquals("testuser", user.getUname());

        user.setPassword("password");
        assertEquals("password", user.getPassword());

        user.setAccessToken("access123");
        assertEquals("access123", user.getAccessToken());

        user.setRefreshToken("refresh456");
        assertEquals("refresh456", user.getRefreshToken());
    }

    @Test
    void testAllArgsConstructor() {
        // Assuming an all-args constructor exists (e.g., via Lombok @AllArgsConstructor)
        // Based on previous coverage report, an all-args constructor was present
        User user = new User(10L, "allargs@example.com", "allargsuser", "securepass", "allTokenAccess", "allTokenRefresh");

        assertEquals(10L, user.getId());
        assertEquals("allargs@example.com", user.getEmail());
        assertEquals("allargsuser", user.getUname());
        assertEquals("securepass", user.getPassword());
        assertEquals("allTokenAccess", user.getAccessToken());
        assertEquals("allTokenRefresh", user.getRefreshToken());
    }
}
