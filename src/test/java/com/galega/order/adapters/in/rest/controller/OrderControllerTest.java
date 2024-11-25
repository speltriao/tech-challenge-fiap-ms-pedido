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
		createOrderDTO.setCustomerId("123");
		createOrderDTO.setProducts(Arrays.asList(new OrderProductDTO("product1", 2), new OrderProductDTO("product2", 1)));

		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"customerId\":\"123\",\"products\":[{\"productId\":\"product1\",\"quantity\":2},{\"productId\":\"product2\",\"quantity\":1}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetOrderById() throws Exception {
		UUID orderId = UUID.randomUUID();
		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

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

		when(iOrderUseCase.updateStatus(eq(orderId), eq(orderStatus), eq(true)))
				.thenReturn(true);

		mockMvc.perform(patch("/orders/{id}", orderId)
						.contentType("application/json")
						.content("{\"status\": \"" + orderStatus.toString() + "\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateOrderMissingField() throws Exception {
		CreateOrderDTO createOrderDTO = new CreateOrderDTO();
		createOrderDTO.setProducts(Arrays.asList(new OrderProductDTO("product1", 2)));

		mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content("{\"products\":[{\"productId\":\"product1\",\"quantity\":2}]}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetOrderHistory() throws Exception {
		UUID orderId = UUID.randomUUID();
		var list = Arrays.asList(new OrderHistory(), new OrderHistory());
		when(iOrderUseCase.getOrderHistory(orderId)).thenReturn(list);

		mockMvc.perform(get("/orders/{id}/history", orderId))
				.andExpect(status().isOk());

		verify(iOrderUseCase, times(1)).getOrderHistory(orderId);
	}

	@Test
	void testGetOrder() throws Exception {
		UUID orderId = UUID.randomUUID();
		Order order = new Order();
		OrderDTO orderDTO = new OrderDTO(order);

		when(iOrderUseCase.get(orderId)).thenReturn(order);

		mockMvc.perform(get("/orders/{id}", orderId.toString()))
				.andExpect(status().isOk());

		verify(iOrderUseCase, times(1)).get(orderId);
	}

	@Test
	void testGetOrdersWithFilters() throws Exception {
		var order1 = new Order();
		var order2 = new Order();
		var orders = List.of(order1, order2);

		when(iOrderUseCase.getAll(any(OrderFilters.class))).thenReturn(orders);

		mockMvc.perform(get("/orders")
						.param("status", "CREATED")
						.param("orderBy", "CREATED_AT")
						.param("orderDirection", "ASC"))
				.andExpect(status().isOk());

		verify(iOrderUseCase).getAll(any(OrderFilters.class));
	}

	@Test
	void testGetOrdersWithoutFilters() throws Exception {
		var order1 = new Order();
		var order2 = new Order();
		var orders = List.of(order1, order2);

		when(iOrderUseCase.getDefaultListOrders()).thenReturn(orders);

		mockMvc.perform(get("/orders"))
				.andExpect(status().isOk());
	}
}
