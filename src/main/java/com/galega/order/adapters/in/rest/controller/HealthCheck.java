package com.galega.order.adapters.in.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check Controller")
@RestController
public class HealthCheck {
	@Operation(summary = "Healthcheck")
	@GetMapping("/ping")
	public ResponseEntity<String> pong(){
		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Pong");
	}
}
