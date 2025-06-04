package com.example.user.controller;

import com.example.user.controller.model.LoginRequest;
import com.example.user.controller.model.SignUpRequest;
import com.example.user.controller.model.LoginRequest;
import com.example.user.controller.model.SignUpRequest;
import com.example.user.service.UserService;
import com.example.user.service.exception.UserAuthenticationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/v1/signUp")
	public ResponseEntity<?> postSignUpV1(@RequestBody SignUpRequest signUp) {
		String msg = userService.registerUser(
				signUp.getUsername(),
				signUp.getPassword(),
				signUp.getEmail()
		);
		if ("User already exists".equals(msg)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", msg));
		}
		return ResponseEntity.ok().body(Map.of("message", "success " + msg));
	}

	@PostMapping("/v1/login")
	public ResponseEntity<?> postLoginV1(@RequestBody LoginRequest loginRequest) {
		try {
			Map<String, String> loginResult = userService.loginRequest(
					loginRequest.getUsername(),
					loginRequest.getPassword()
			);
			return ResponseEntity.ok().body(loginResult);
		} catch (UserAuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		}
	}
}
