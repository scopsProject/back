package com.example.projectNameBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProjectNameBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectNameBackApplication.class, args);
	}
}