package com.example.user.service;

import com.example.user.persistance.dto.User;
import com.example.user.persistance.UserRepository;
import com.example.user.service.exception.UserAuthenticationException;
import com.example.user.service.redis.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final TokenService tokenService;

	public String registerUser(String name, String password, String email) {
		Optional<User> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent()) {
			return "User already exists";
		} else {
			User newUser = new User();
			newUser.setUname(name);
			newUser.setEmail(email);
			newUser.setPassword(password);
			return userRepository.save(newUser).getUname();
		}
	}

	public Map<String, String> loginRequest(String username, String password) throws UserAuthenticationException {
		Optional<User> userOptional = userRepository.findByUnameAndPassword(username, password);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String accessToken = tokenService.generateToken();
			String refreshToken = tokenService.generateToken(); // Generate a separate refresh token

			tokenService.storeToken(user.getId().toString(), "accessToken", accessToken);
			tokenService.storeToken(user.getId().toString(), "refreshToken", refreshToken);

			Map<String, String> response = new HashMap<>();
			response.put("message", "Authenticated");
			response.put("accessToken", accessToken);
			response.put("refreshToken", refreshToken);
			return response;
		} else {
			throw new UserAuthenticationException("Not Authenticated");
		}
	}
}
