package com.example.user.service.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService implements TokenService {
    private final StringRedisTemplate redisTemplate;
    private static final long TOKEN_EXPIRATION_HOURS = 24; // Example expiration

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void storeToken(String userId, String tokenType, String token) {
        String key = "user:" + userId + ":" + tokenType;
        redisTemplate.opsForValue().set(key, token, TOKEN_EXPIRATION_HOURS, TimeUnit.HOURS);
    }

    @Override
    public String retrieveToken(String userId, String tokenType) {
        String key = "user:" + userId + ":" + tokenType;
        return redisTemplate.opsForValue().get(key);
    }
}
