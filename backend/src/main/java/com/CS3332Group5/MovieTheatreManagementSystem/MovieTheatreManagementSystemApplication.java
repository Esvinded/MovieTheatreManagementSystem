package com.CS3332Group5.MovieTheatreManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieTheatreManagementSystemApplication {

	public static void main(String[] args) {
	    SpringApplication.run(MovieTheatreManagementSystemApplication.class, args);
	}

}
