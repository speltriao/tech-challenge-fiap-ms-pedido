/**
 * This file contains tests for the OrderController following the principles of Behavior-Driven Development (BDD).
 *
 * BDD focuses on describing the system's behavior using natural language structures:
 * - Given: the initial context or setup.
 * - When: the action to be performed.
 * - Then: the expected outcome or result.
 *
 * This approach improves test readability and aligns development with business requirements.
 */
package com.galega.order.adapters.in.rest.controller;

import com.galega.order.adapters.in.rest.dto.CreateOrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderDTO;
import com.galega.order.adapters.in.rest.dto.OrderProductDTO;
import com.galega.order.adapters.in.rest.dto.UpdateOrderStatusDTO;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.usecase.IOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
		// Given a valid request to create an order
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setCustomerId("123e4567-e89b-12d3-a456-426614174000");
		createOrderDTO.setProducts(Arrays.asList(
				new OrderProductDTO("123e4567-e89b-12d3-a456-426614174001", 2),
				new OrderProductDTO("123e4567-e89b-12d3-a456-426614174002", 1)
		));

		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

		when(iOrderUseCase.create(any(Order.class))).thenReturn(order);
		doNothing().when(sqsOutHandler).sendOrderMessage(any(OrderDTO.class));

		// When the order creation endpoint is called
		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"customerId\":\"123e4567-e89b-12d3-a456-426614174000\","
								+ "\"products\":["
								+ "{\"id\":\"123e4567-e89b-12d3-a456-426614174001\",\"quantity\":2},"
								+ "{\"id\":\"123e4567-e89b-12d3-a456-426614174002\",\"quantity\":1}"
								+ "]}"))
				// Then the response should be 201 Created
				.andExpect(status().isCreated());
	}

	@Test
	void testCreateOrderInvalidCustomerId() throws Exception {
		// Given an invalid customer ID in the request
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setCustomerId("123");
		createOrderDTO.setProducts(Arrays.asList(
				new OrderProductDTO("product1", 2),
				new OrderProductDTO("product2", 1)
		));

		// When the order creation endpoint is called
		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"customerId\":\"123\",\"products\":[{\"productId\":\"product1\",\"quantity\":2},{\"productId\":\"product2\",\"quantity\":1}]}"))
				// Then the response should be 400 Bad Request
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetOrderById() throws Exception {
		// Given an existing order ID
		UUID orderId = UUID.randomUUID();
		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

		when(iOrderUseCase.get(orderId)).thenReturn(order);

		// When the GET endpoint is called with the order ID
		mockMvc.perform(get("/orders/{id}", orderId))
				// Then the response should be 200 OK
				.andExpect(status().isOk());
	}

	@Test
	void testUpdateOrderStatus() throws Exception {
		// Given a valid request to update the order status
		var orderStatus = OrderStatusEnum.CREATED;
		UUID orderId = UUID.randomUUID();
		UpdateOrderStatusDTO updateOrderStatusDTO = new UpdateOrderStatusDTO();
		updateOrderStatusDTO.setStatus(orderStatus.toString());

		when(iOrderUseCase.updateStatus(eq(orderId), eq(orderStatus), eq(true)))
				.thenReturn(true);

		// When the PATCH endpoint is called with the new status
		mockMvc.perform(patch("/orders/{id}", orderId)
						.contentType("application/json")
						.content("{\"status\": \"" + orderStatus.toString() + "\"}"))
				// Then the response should be 200 OK
				.andExpect(status().isOk());
	}

	@Test
	void testCreateOrderMissingField() throws Exception {
		// Given a request with missing fields
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setProducts(Arrays.asList(new OrderProductDTO("product1", 2)));

		// When the order creation endpoint is called
		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"products\":[{\"productId\":\"product1\",\"quantity\":2}]}"))
				// Then the response should be 400 Bad Request
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetOrderHistory() throws Exception {
		// Given an order ID with a history
		UUID orderId = UUID.randomUUID();
		var list = Arrays.asList(new OrderHistory(), new OrderHistory());
		when(iOrderUseCase.getOrderHistory(orderId)).thenReturn(list);

		// When the GET endpoint is called for order history
		mockMvc.perform(get("/orders/{id}/history", orderId))
				// Then the response should be 200 OK
				.andExpect(status().isOk());

		verify(iOrderUseCase, times(1)).getOrderHistory(orderId);
	}

	@Test
	void testGetOrder() throws Exception {
		// Given an existing order ID
		UUID orderId = UUID.randomUUID();
		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

		when(iOrderUseCase.get(orderId)).thenReturn(order);

		// When the GET endpoint is called with the order ID
		mockMvc.perform(get("/orders/{id}", orderId.toString()))
				// Then the response should be 200 OK
				.andExpect(status().isOk());

		verify(iOrderUseCase, times(1)).get(orderId);
	}

	@Test
	void testGetOrdersWithFilters() throws Exception {
		// Given filters to search for orders
		var order1 = new Order();
		var order2 = new Order();
		var orders = List.of(order1, order2);

		when(iOrderUseCase.getAll(any(OrderFilters.class))).thenReturn(orders);

		// When the GET endpoint is called with filters
		mockMvc.perform(get("/orders")
						.param("status", "CREATED")
						.param("orderBy", "CREATED_AT")
						.param("orderDirection", "ASC"))
				// Then the response should be 200 OK
				.andExpect(status().isOk());

		verify(iOrderUseCase).getAll(any(OrderFilters.class));
	}

	@Test
	void testGetOrdersWithoutFilters() throws Exception {
		// Given no filters for retrieving orders
		var order1 = new Order();
		var order2 = new Order();
		var orders = List.of(order1, order2);

		when(iOrderUseCase.getDefaultListOrders()).thenReturn(orders);

		// When the GET endpoint is called without filters
		mockMvc.perform(get("/orders"))
				// Then the response should be 200 OK
				.andExpect(status().isOk());
	}
}