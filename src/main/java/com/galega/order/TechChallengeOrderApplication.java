package com.galega.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechChallengeOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechChallengeOrderApplication.class, args);
	}
}
