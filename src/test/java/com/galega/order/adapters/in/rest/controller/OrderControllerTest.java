package com.galega.order.adapters.in.rest.controller;

import com.galega.order.adapters.BaseSQSHandler;
import com.galega.order.adapters.in.rest.dto.CreateOrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderProductDTO;
import com.galega.order.adapters.in.rest.dto.UpdateOrderStatusDTO;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.service.OrderService;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)

public class OrderControllerTest {

	private MockMvc mockMvc;

	@Mock
	private IOrderUseCase iOrderUseCase;

	@Mock
	private SQSOutHandler sqsOutHandler;

	@InjectMocks
	private OrderController orderController;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
	}

	@Test
	void testCreateOrderValidRequest() throws Exception {
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setCustomerId("123e4567-e89b-12d3-a456-426614174000"); // Valid UUID
		createOrderDTO.setProducts(Arrays.asList(
				new OrderProductDTO("123e4567-e89b-12d3-a456-426614174001", 2), // Valid UUID
				new OrderProductDTO("123e4567-e89b-12d3-a456-426614174002", 1)  // Valid UUID
		));

		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

		// Mocking the service call
		when(iOrderUseCase.create(any(Order.class))).thenReturn(order);

		// Mocking the SQS call
		doNothing().when(sqsOutHandler).sendOrderMessage(any(OrderDTO.class));

		// MockMvc performs the POST request with valid JSON
		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"customerId\":\"123e4567-e89b-12d3-a456-426614174000\","
								+ "\"products\":["
								+ "{\"id\":\"123e4567-e89b-12d3-a456-426614174001\",\"quantity\":2},"
								+ "{\"id\":\"123e4567-e89b-12d3-a456-426614174002\",\"quantity\":1}"
								+ "]}"))
				.andExpect(status().isCreated());
	}



	@Test
	void testCreateOrderInvalidCustomerId() throws Exception {
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setCustomerId("123");  // Invalid customerId length
		createOrderDTO.setProducts(Arrays.asList(new OrderProductDTO("product1", 2), new OrderProductDTO("product2", 1)));

		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"customerId\":\"123\",\"products\":[{\"productId\":\"product1\",\"quantity\":2},{\"productId\":\"product2\",\"quantity\":1}]}"))
				.andExpect(status().isBadRequest());  // Expecting a 400 BadRequest due to invalid customerId
	}

	@Test
	void testGetOrderById() throws Exception {
		UUID orderId = UUID.randomUUID();
		Order order = new Order();  // Add properties if needed
		OrderDTO orderDTO = new OrderDTO(order);

		// Mocking the service call
		when(iOrderUseCase.get(orderId)).thenReturn(order);

		mockMvc.perform(get("/orders/{id}", orderId))
				.andExpect(status().isOk());
	}

	@Test
	void testUpdateOrderStatus() throws Exception {
		var orderStatus = OrderStatusEnum.CREATED;
		UUID orderId = UUID.randomUUID();
		UpdateOrderStatusDTO updateOrderStatusDTO = new UpdateOrderStatusDTO();
		updateOrderStatusDTO.setStatus(orderStatus.toString());

		// Mocking the service call
		when(iOrderUseCase.updateStatus(eq(orderId), eq(orderStatus)))
				.thenReturn(true);

		// Correct JSON content construction
		mockMvc.perform(patch("/orders/{id}", orderId)
						.contentType("application/json")
						.content("{\"status\": \"" + orderStatus.toString() + "\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateOrderMissingField() throws Exception {
		// Test missing customerId field
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setProducts(Arrays.asList(new OrderProductDTO("product1", 2)));

		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"products\":[{\"productId\":\"product1\",\"quantity\":2}]}"))
				.andExpect(status().isBadRequest()); // Expecting BadRequest due to missing customerId
	}
}
