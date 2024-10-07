package com.example.demo;

import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCosmosRepositories(basePackages = "com.example.demo.fileupload.cosmos.repo")
public class LearnAzureApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnAzureApplication.class, args);
	}

}
