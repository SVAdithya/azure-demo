package com.example.user.service;

import com.example.user.persistance.dto.User;
import com.example.user.persistance.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public String registerUser(String name, String password, String email) {
		Optional<User> existingUser = userRepository.findByEmail(email).stream().findFirst()
				.filter(user -> email.equals(user.getEmail()));
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

	public String loginRequest(String username, String password) throws AuthenticationException {
		boolean isAuthenticated = userRepository.findByUnameAndPassword(username, password).isPresent();
		if (isAuthenticated) {
			return "Authenticated";
		} else {
			throw new AuthenticationException("Not Authenticated") ;
		}
	}
}
