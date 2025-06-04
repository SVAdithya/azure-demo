package com.example.user.controller;

import com.example.user.controller.model.LoginRequest;
import com.example.user.controller.model.SignUpRequest;
import com.example.user.service.UserService;
import com.example.user.service.exception.UserAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testPostSignUpV1_Success() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");

        when(userService.registerUser("testuser", "password123", "test@example.com"))
                .thenReturn("testuser");

        mockMvc.perform(post("/v1/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success testuser"));

        verify(userService).registerUser("testuser", "password123", "test@example.com");
    }

    @Test
    void testPostSignUpV1_UserAlreadyExists() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("existinguser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("existing@example.com");

        when(userService.registerUser("existinguser", "password123", "existing@example.com"))
                .thenReturn("User already exists");

        mockMvc.perform(post("/v1/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already exists"));

        verify(userService).registerUser("existinguser", "password123", "existing@example.com");
    }

    @Test
    void testPostLoginV1_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        Map<String, String> successResponse = Map.of(
                "message", "Authenticated",
                "accessToken", "dummyAccess",
                "refreshToken", "dummyRefresh"
        );
        when(userService.loginRequest("testuser", "password123")).thenReturn(successResponse);

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Authenticated"))
                .andExpect(jsonPath("$.accessToken").value("dummyAccess"))
                .andExpect(jsonPath("$.refreshToken").value("dummyRefresh"));

        verify(userService).loginRequest("testuser", "password123");
    }

    @Test
    void testPostLoginV1_AuthenticationFailure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpass");

        when(userService.loginRequest("wronguser", "wrongpass"))
                .thenThrow(new UserAuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(userService).loginRequest("wronguser", "wrongpass");
    }
}
