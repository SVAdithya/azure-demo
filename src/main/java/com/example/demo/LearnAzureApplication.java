package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LearnAzureApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnAzureApplication.class, args);
	}

	@GetMapping("/v1/hello")
	public String showDemo(){
		return "Hey, welcome to Azure";
	}

}
