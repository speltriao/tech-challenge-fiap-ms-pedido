package com.fiap.techchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.fiap.techchallenge.handlers",
		"com.fiap.techchallenge.controller",
		"com.fiap.techchallenge.drivers",
		"com.fiap.techchallenge.infrastructure.server"
})
public class TechChallengeApplication {

	public static void main(String[] args) {

		SpringApplication.run(TechChallengeApplication.class, args);
	}

}
