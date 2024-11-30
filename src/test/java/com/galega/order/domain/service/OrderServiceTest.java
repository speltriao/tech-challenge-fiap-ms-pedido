package com.galega.order.domain.service;

import com.galega.order.domain.entity.*;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.PaymentStatusEnum;
import com.galega.order.domain.enums.ProductCategoryEnum;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;
import com.galega.order.domain.repository.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

	@Mock
	private OrderRepositoryPort orderRepositoryPort;

	@Mock
	private DataSource dataSource;

	@Mock
	private ProductService productService;

	@InjectMocks
	private OrderService orderService;

	private Order order;
	private UUID orderId;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		orderId = UUID.randomUUID();
		order = new Order();
		order.setId(orderId);
		order.setStatus(OrderStatusEnum.CREATED);
		order.setCreatedAt(LocalDateTime.now());
	}

	@Test
	void testGetAll() {
		OrderFilters filters = new OrderFilters();
		List<Order> mockOrders = List.of(order);

		when(orderRepositoryPort.getAll(filters)).thenReturn(mockOrders);

		List<Order> orders = orderService.getAll(filters);

		assertNotNull(orders);
		assertEquals(1, orders.size());
		verify(orderRepositoryPort).getAll(filters);
	}

	@Test
	void testGet() throws EntityNotFoundException {
		when(orderRepositoryPort.getByIdWithProducts(orderId)).thenReturn(order);
		when(orderRepositoryPort.getOrderHistoryByOrderId(orderId)).thenReturn(List.of(new OrderHistory()));

		Order result = orderService.get(orderId);

		assertNotNull(result);
		assertEquals(orderId, result.getId());
		verify(orderRepositoryPort).getByIdWithProducts(orderId);
		verify(orderRepositoryPort).getOrderHistoryByOrderId(orderId);
	}

	@Test
	void testGetOrderNotFound() {
		when(orderRepositoryPort.getByIdWithProducts(orderId)).thenReturn(null);

		assertThrows(EntityNotFoundException.class, () -> orderService.get(orderId));
	}

	@Test
	void testCreateOrder() throws EntityNotFoundException {
		UUID productId = UUID.randomUUID();
		var productFromDb = new Product(productId, "Phone", "Smartphone", "url", BigDecimal.valueOf(500), ProductCategoryEnum.DRINK);

		var productInOrder = new Product(productId, null, null, null, null, null); // Minimal data for the product
		var productAndQuantity = new ProductAndQuantity(productInOrder, 2);
		var order = new Order();
		order.setProducts(List.of(productAndQuantity));

		when(productService.getById(productId)).thenReturn(productFromDb);

		when(orderRepositoryPort.create(order)).thenAnswer(invocation -> {
			Order createdOrder = invocation.getArgument(0);
			createdOrder.setId(UUID.randomUUID());
			return 1;
		});

		Order createdOrder = orderService.create(order);

		assertNotNull(createdOrder);
		assertNotNull(createdOrder.getId());
		assertEquals(OrderStatusEnum.CREATED, createdOrder.getStatus());
		assertNotNull(createdOrder.getCreatedAt());

		verify(productService).getById(productId);
		verify(orderRepositoryPort).create(order);
	}

	@Test
	void testCreateOrderWithInvalidQuantity() {
		var productInOrder = new Product(UUID.randomUUID(), null, null, null, null, null);
		var productAndQuantity = new ProductAndQuantity(productInOrder, 0); // Invalid quantity
		var order = new Order();
		order.setProducts(List.of(productAndQuantity));

		var exception = assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
		assertEquals("Quantity must be greater than zero", exception.getMessage());

		verifyNoInteractions(orderRepositoryPort);
	}

	@Test
	void testCreateOrderWithNonExistentProduct() {
		UUID nonExistentProductId = UUID.randomUUID();

		when(productService.getById(nonExistentProductId)).thenReturn(null);

		verifyNoInteractions(orderRepositoryPort);
	}


	@Test
	void testCreateOrderWithInvalidProduct() {
		order.setProducts(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
		assertEquals("Order must have at least one product", exception.getMessage());
	}

	@Test
	void testUpdateStatus() throws OrderAlreadyWithStatusException, EntityNotFoundException {
		when(orderRepositoryPort.getById(orderId)).thenReturn(order);
		when(orderRepositoryPort.updateStatus(any(), any(), any())).thenReturn(2);

		boolean updated = orderService.updateStatus(orderId, OrderStatusEnum.CANCELED, false);

		assertTrue(updated);
	}

	@Test
	void testUpdateStatusOrderNotFound() {
		when(orderRepositoryPort.getById(orderId)).thenReturn(null);

		assertThrows(EntityNotFoundException.class, () -> orderService.updateStatus(orderId, OrderStatusEnum.IN_PREPARATION, false));
	}

	@Test
	void testUpdateStatusInvalidTransition() {
		order.setStatus(OrderStatusEnum.FINISHED);
		when(orderRepositoryPort.getById(orderId)).thenReturn(order);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(orderId, OrderStatusEnum.READY_TO_DELIVERY, false));
		assertEquals("Order must be in 'IN_PREPARATION' status", exception.getMessage());
	}

	@Test
	void testProcessOrderPayment() throws OrderAlreadyWithStatusException, EntityNotFoundException {
		var order = new Order();
		order.setId(orderId);
		order.setStatus(OrderStatusEnum.CREATED);
		order.setCreatedAt(LocalDateTime.now());
		when(orderRepositoryPort.getById(orderId)).thenReturn(order);

		boolean result = orderService.processOrderPayment(orderId, PaymentStatusEnum.APPROVED);

		assertTrue(result);
		assertEquals(OrderStatusEnum.CREATED, order.getStatus());
	}

	@Test
	void testProcessOrderPaymentNotApproved() throws OrderAlreadyWithStatusException, EntityNotFoundException {
		when(orderRepositoryPort.getById(orderId)).thenReturn(order);

		boolean result = orderService.processOrderPayment(orderId, PaymentStatusEnum.REFUSED);

		assertFalse(result);
		verify(orderRepositoryPort, never()).updateStatus(any(), any(), any());
	}

	@Test
	void testGetDefaultListOrders() {
		List<Order> mockOrders = List.of(order);
		when(orderRepositoryPort.getAll(null)).thenReturn(mockOrders);

		List<Order> orders = orderService.getDefaultListOrders();

		assertNotNull(orders);
		verify(orderRepositoryPort).getAll(null);
	}
}
