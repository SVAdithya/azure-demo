package com.example.demo.fileupload.persistance;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}
