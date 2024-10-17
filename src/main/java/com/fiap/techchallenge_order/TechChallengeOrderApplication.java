package com.fiap.techchallenge_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.fiap.techchallenge_order.adapters"
})
public class TechChallengeOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechChallengeOrderApplication.class, args);
	}

}
