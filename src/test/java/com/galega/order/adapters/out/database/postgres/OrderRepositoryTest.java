package com.galega.order.adapters.out.database.postgres;

import com.galega.order.adapters.in.queue.sqs.handler.SQSInHandler;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.*;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.ProductCategoryEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@Import(DataBaseTestConfig.class)
public class OrderRepositoryTest {

	@MockBean
	private SQSInHandler sqsInHandler;

	@MockBean
	private SQSOutHandler sqsOutHandler;

	@Autowired
	@Qualifier("testDataSource")
	private DataSource testDataSource;

	@Autowired
	@Qualifier("testFlyway")
	private Flyway flyway;

	private OrderRepository orderRepository;
	private ProductRepository productRepository;


	@BeforeEach
	void setUp() {
		orderRepository = new OrderRepository(testDataSource);
		productRepository = new ProductRepository(testDataSource);
		flyway.clean();
		flyway.migrate();
	}

	@Test
	void testCreateOrder() {
		// Arrange
		Order order = createSampleOrder();

		// Act
		int rowsAffected = orderRepository.create(order);

		// Assert
		assertEquals(1, rowsAffected);
		Order fetchedOrder = orderRepository.getByIdWithProducts(order.getId());
		assertNotNull(fetchedOrder);
		assertEquals(order.getCustomerId(), fetchedOrder.getCustomerId());
		assertEquals(order.getProducts().size(), fetchedOrder.getProducts().size());
	}

	@Test
	void testGetAllOrdersWithFilter() {
		// Arrange
		Order order1 = createSampleOrder();
		order1.setStatus(OrderStatusEnum.CREATED);
		Order order2 = createSampleOrder();
		order2.setStatus(OrderStatusEnum.RECEIVED);
		orderRepository.create(order1);
		orderRepository.create(order2);

		// Act
		OrderFilters filters = new OrderFilters(OrderStatusEnum.RECEIVED, null, null);
		List<Order> orders = orderRepository.getAll(filters);

		// Assert
		assertEquals(1, orders.size());
		assertEquals(order2.getId(), orders.get(0).getId());
	}

	@Test
	void testGetAllOrdersWithoutFilter() {
		Order order1 = createSampleOrder();
		Order order2 = createSampleOrder();
		orderRepository.create(order1);
		orderRepository.create(order2);

		List<Order> orders = orderRepository.getAll(null);

		assertEquals(2, orders.size());
	}


	@Test
	void testUpdateOrderStatus() {
		// Arrange
		Order order = createSampleOrder();
		orderRepository.create(order);
		OrderStatusEnum newStatus = OrderStatusEnum.IN_PREPARATION;

		// Act
		int rowsAffected = orderRepository.updateStatus(order, newStatus, order.getStatus());

		// Assert
		assertEquals(2, rowsAffected); // 1 for update, 1 for history insertion
		Order updatedOrder = orderRepository.getById(order.getId());
		assertEquals(newStatus, updatedOrder.getStatus());

		List<OrderHistory> history = orderRepository.getOrderHistoryByOrderId(order.getId());
		assertEquals(1, history.size());
		assertEquals(OrderStatusEnum.CREATED, history.get(0).getPreviousStatus());
	}

	@Test
	void testGetOrderByIdWithProducts() {
		// Arrange
		Order order = createSampleOrder();
		orderRepository.create(order);

		// Act
		Order fetchedOrder = orderRepository.getByIdWithProducts(order.getId());

		// Assert
		assertNotNull(fetchedOrder);
		assertEquals(order.getProducts().size(), fetchedOrder.getProducts().size());
		assertEquals(order.getProducts().get(0).getProduct().getName(),
				fetchedOrder.getProducts().get(0).getProduct().getName());
	}

	private Order createSampleOrder() {
		var product = new Product(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(5),"Description", "image_url",
				BigDecimal.valueOf(49.99), ProductCategoryEnum.DESSERT);
		productRepository.create(product);
		return new Order(
				UUID.randomUUID(),
				UUID.randomUUID(),
				12345,
				BigDecimal.valueOf(99.99),
				OrderStatusEnum.CREATED,
				LocalDateTime.now(),
				0,
				List.of(new ProductAndQuantity(
						product, 2)),
				List.of(),
				null
		);
	}
}
