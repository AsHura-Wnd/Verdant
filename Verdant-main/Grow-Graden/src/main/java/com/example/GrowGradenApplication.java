package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot entry point for the Verdant online gardening community platform.
 */
@SpringBootApplication
public class GrowGradenApplication {

	/**
	 * Launches the Spring Boot embedded server and application context.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GrowGradenApplication.class, args);
	}

}
