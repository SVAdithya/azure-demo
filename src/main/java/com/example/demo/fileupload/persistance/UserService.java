package com.example.demo.fileupload.persistance;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public User registerOrUpdateUser(String name, String password, String email) {
		return userRepository.findByEmail(email)
				.orElseGet(() -> {
					User newUser = new User();
					newUser.setUname(name);
					newUser.setEmail(email);
					newUser.setPassword(password);
					return userRepository.save(newUser);
				});
	}
}
