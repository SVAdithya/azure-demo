package com.example.demo.fileupload;

import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCosmosRepositories(basePackages = "com.example.demo.fileupload.cosmos.repo")
@EnableJpaRepositories(basePackages = "com.example.demo.fileupload.persistance")
public class LearnAzureApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnAzureApplication.class, args);
	}

}
