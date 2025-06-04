package com.example.user.service.redis;

public interface TokenService {
    String generateToken();
    void storeToken(String userId, String tokenType, String token);
    String retrieveToken(String userId, String tokenType);
    // Potentially add methods for token validation or deletion
}
