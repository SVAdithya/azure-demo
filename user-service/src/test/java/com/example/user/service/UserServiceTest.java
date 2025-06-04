package com.example.user.service;

import com.example.user.persistance.UserRepository;
import com.example.user.persistance.dto.User;
import com.example.user.service.exception.UserAuthenticationException;
import com.example.user.service.redis.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L); // Important for token storage key
        sampleUser.setUname("testUser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("password123"); // Raw password, assuming no hashing in this service layer
    }

    // Test methods for registerUser_Success
    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Simulate setting the ID on the user object passed to save,
            // and ensure the returned user object from save has this ID.
            // This also means the User object inside registerUser will have the ID set.
            User userToSave = invocation.getArgument(0);
            userToSave.setId(sampleUser.getId()); // Simulate ID assignment by JPA
            return userToSave;
        });

        String result = userService.registerUser(sampleUser.getUname(), sampleUser.getPassword(), sampleUser.getEmail());

        assertEquals(sampleUser.getUname(), result);
        verify(userRepository).save(any(User.class));
        verify(userRepository).findByEmail(sampleUser.getEmail());
    }

    // Test methods for registerUser_UserAlreadyExists
    @Test
    void testRegisterUser_UserAlreadyExists() {
        // Use a specific email for this test case to ensure no interference
        String existingEmail = "existing@example.com";
        User existingUser = new User();
        existingUser.setEmail(existingEmail);

        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        String result = userService.registerUser("anotherUser", "anotherPass", existingEmail);

        assertEquals("User already exists", result);
        verify(userRepository, never()).save(any(User.class));
        verify(userRepository).findByEmail(existingEmail);
    }

    // Test methods for loginRequest_Success
    @Test
    void testLoginRequest_Success() throws UserAuthenticationException {
        when(userRepository.findByUnameAndPassword(sampleUser.getUname(), sampleUser.getPassword()))
                .thenReturn(Optional.of(sampleUser)); // sampleUser already has ID 1L from setUp
        when(tokenService.generateToken()).thenReturn("dummyAccessToken", "dummyRefreshToken");

        Map<String, String> result = userService.loginRequest(sampleUser.getUname(), sampleUser.getPassword());

        assertNotNull(result);
        assertEquals("Authenticated", result.get("message"));
        assertEquals("dummyAccessToken", result.get("accessToken"));
        assertEquals("dummyRefreshToken", result.get("refreshToken"));

        // Verify that storeToken is called with the user's ID
        verify(tokenService).storeToken(sampleUser.getId().toString(), "accessToken", "dummyAccessToken");
        verify(tokenService).storeToken(sampleUser.getId().toString(), "refreshToken", "dummyRefreshToken");
        verify(userRepository).findByUnameAndPassword(sampleUser.getUname(), sampleUser.getPassword());
        verify(tokenService, times(2)).generateToken(); // Called for access and refresh token
    }

    // Test methods for loginRequest_AuthenticationFailure
    @Test
    void testLoginRequest_AuthenticationFailure() {
        when(userRepository.findByUnameAndPassword(anyString(), anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserAuthenticationException.class, () -> {
            userService.loginRequest("wrongUser", "wrongPass");
        });

        assertEquals("Not Authenticated", exception.getMessage());
        verify(tokenService, never()).generateToken();
        verify(tokenService, never()).storeToken(anyString(), anyString(), anyString());
        verify(userRepository).findByUnameAndPassword("wrongUser", "wrongPass");
    }
}
