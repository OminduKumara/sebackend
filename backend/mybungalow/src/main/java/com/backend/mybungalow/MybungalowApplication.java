package com.backend.mybungalow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MybungalowApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybungalowApplication.class, args);
	}

}
