package com.example.user.controller.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    void testDefaultConstructorAndAccessors() {
        LoginRequest loginRequest = new LoginRequest(); // Test default constructor

        loginRequest.setUsername("testUser");
        assertEquals("testUser", loginRequest.getUsername());

        loginRequest.setPassword("testPass");
        assertEquals("testPass", loginRequest.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Assuming an all-args constructor exists (e.g., via Lombok @AllArgsConstructor)
        LoginRequest loginRequest = new LoginRequest("allArgsUser", "allArgsPass");

        assertEquals("allArgsUser", loginRequest.getUsername());
        assertEquals("allArgsPass", loginRequest.getPassword());
    }

    // Test for no-args constructor if it's distinct and needed,
    // but Lombok @Data or @NoArgsConstructor + @AllArgsConstructor usually covers this.
    // The default constructor test above handles the @NoArgsConstructor case.
}
