package com.galega.order.adapters.out.database.postgres;

import com.galega.order.adapters.in.queue.sqs.handler.SQSInHandler;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.Product;
import com.galega.order.domain.enums.ProductCategoryEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductRepositoryTest {

	@MockBean
	private SQSOutHandler sqsOutHandler;

	@MockBean
	private SQSInHandler sqsInHandler;

	// Start a PostgreSQL container automatically
	@Container
	private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
			.withDatabaseName("galega")
			.withUsername("postgres")
			.withPassword("postgres");

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		jdbcTemplate.execute("TRUNCATE TABLE public.product CASCADE;");
	}

	@Test
	void testCreateProduct() {
		Product product = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url", BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);
		int rowsAffected = productRepository.create(product);

		assertEquals(1, rowsAffected);

		Product fetchedProduct = productRepository.getById(product.getId());
		assertNotNull(fetchedProduct);
		assertEquals(product.getName(), fetchedProduct.getName());
		assertEquals(product.getPrice(), fetchedProduct.getPrice());
	}

	@Test
	void testGetAllProductsWithCategory() {
		Product product1 = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url", BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);
		Product product2 = new Product(UUID.randomUUID(), "Product 2", "Description", "image_url", BigDecimal.valueOf(20.99), ProductCategoryEnum.SIDE_DISH);
		productRepository.create(product1);
		productRepository.create(product2);

		Product.ProductFilters filters = new Product.ProductFilters(ProductCategoryEnum.SANDWICH);
		List<Product> products = productRepository.getAll(filters);

		assertEquals(1, products.size());
		assertEquals(product1.getId(), products.get(0).getId());
	}

	@Test
	void testDeleteProduct() {
		Product product = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url", BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);
		productRepository.create(product);

		int rowsAffected = productRepository.delete(product.getId());

		assertEquals(1, rowsAffected);

		Product deletedProduct = productRepository.getById(product.getId());
		assertNull(deletedProduct);
	}
}
