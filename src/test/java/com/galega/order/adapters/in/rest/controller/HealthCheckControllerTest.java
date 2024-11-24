package com.galega.order.adapters.in.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(classes = HealthCheckController.class)
public class HealthCheckControllerTest {


	@Autowired
	private HealthCheckController controller;

	@Test
	void shouldReturnPongWhenPingEndpointIsCalled() {
		ResponseEntity<String> response = controller.pong();

		assertEquals(200, response.getStatusCode().value());
		assertEquals("Pong", response.getBody());
	}
}