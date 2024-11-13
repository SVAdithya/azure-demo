package com.example.demo.fileupload.user;

import com.example.demo.fileupload.persistance.UserService;
import com.example.demo.fileupload.user.model.SignUpRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/v1/signUp")
	public String postSignUpV1(@RequestBody SignUpRequest signUp) {
		String msg = userService.registerUser(
				signUp.getUsername(),
				signUp.getPassword(),
				signUp.getEmail()
		);
		return "success " + msg;
	}

}
