package com.example.user.service.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisTokenServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisTokenService redisTokenService;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> valueCaptor;

    @Captor
    private ArgumentCaptor<Long> timeoutCaptor;

    @Captor
    private ArgumentCaptor<TimeUnit> timeUnitCaptor;

    @BeforeEach
    void setUp() {
        redisTokenService = new RedisTokenService(stringRedisTemplate);
    }

    @Test
    void testConstructorInitializes() {
        assertNotNull(redisTokenService);
    }

    @Test
    void testGenerateToken() {
        String token = redisTokenService.generateToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Optional: Check if it's a valid UUID
        try {
            UUID.fromString(token);
        } catch (IllegalArgumentException e) {
            fail("Generated token is not a valid UUID: " + token);
        }
    }

    @Test
    void testStoreToken() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations); // Moved here
        String userId = "user123";
        String tokenType = "accessToken";
        String tokenValue = "tokenAbc";
        String expectedKey = "user:" + userId + ":" + tokenType;

        redisTokenService.storeToken(userId, tokenType, tokenValue);

        // Verify that opsForValue().set() was called with the correct parameters
        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), timeoutCaptor.capture(), timeUnitCaptor.capture());
        assertEquals(expectedKey, keyCaptor.getValue());
        assertEquals(tokenValue, valueCaptor.getValue());
        assertEquals(24L, timeoutCaptor.getValue());
        assertEquals(TimeUnit.HOURS, timeUnitCaptor.getValue());
    }

    @Test
    void testRetrieveToken() {
        String userId = "user456";
        String tokenType = "refreshToken";
        String expectedKey = "user:" + userId + ":" + tokenType;
        String expectedTokenValue = "retrievedTokenXyz";

        // Define mock behavior for get
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations); // Moved here
        when(valueOperations.get(expectedKey)).thenReturn(expectedTokenValue);

        String retrievedToken = redisTokenService.retrieveToken(userId, tokenType);

        // Verify that opsForValue().get() was called with the correct key
        verify(valueOperations).get(keyCaptor.capture());
        assertEquals(expectedKey, keyCaptor.getValue());

        // Assert that the retrieved token is the expected value
        assertEquals(expectedTokenValue, retrievedToken);
    }

    @Test
    void testRetrieveToken_NotFound() {
        String userId = "user789";
        String tokenType = "sessionToken";
        String expectedKey = "user:" + userId + ":" + tokenType;

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations); // Moved here
        when(valueOperations.get(expectedKey)).thenReturn(null); // Simulate token not found

        String retrievedToken = redisTokenService.retrieveToken(userId, tokenType);

        verify(valueOperations).get(expectedKey);
        assertNull(retrievedToken, "Token should be null when not found in Redis");
    }
}
