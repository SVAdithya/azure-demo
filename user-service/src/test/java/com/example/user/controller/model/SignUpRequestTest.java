package com.example.user.controller.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SignUpRequestTest {

    @Test
    void testDefaultConstructorAndAccessors() {
        SignUpRequest signUpRequest = new SignUpRequest(); // Test default constructor

        signUpRequest.setUsername("testUser");
        assertEquals("testUser", signUpRequest.getUsername());

        signUpRequest.setPassword("testPass");
        assertEquals("testPass", signUpRequest.getPassword());

        signUpRequest.setEmail("test@example.com");
        assertEquals("test@example.com", signUpRequest.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        // Assuming an all-args constructor exists (e.g., via Lombok @AllArgsConstructor)
        SignUpRequest signUpRequest = new SignUpRequest("allArgsUser", "allArgsPass", "allargs@example.com");

        assertEquals("allArgsUser", signUpRequest.getUsername());
        assertEquals("allArgsPass", signUpRequest.getPassword());
        assertEquals("allargs@example.com", signUpRequest.getEmail());
    }
}
