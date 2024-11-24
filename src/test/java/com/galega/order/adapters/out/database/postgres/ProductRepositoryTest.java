package com.galega.order.adapters.out.database.postgres;

import com.galega.order.adapters.in.queue.sqs.handler.SQSInHandler;
import com.galega.order.adapters.out.queue.sqs.handler.SQSOutHandler;
import com.galega.order.domain.entity.Product;
import com.galega.order.domain.enums.ProductCategoryEnum;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Import(DataBaseTestConfig.class)
public class ProductRepositoryTest {

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

	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		// Initialize the ProductRepository with the test DataSource before each test
		productRepository = new ProductRepository(testDataSource);

		// Ensure the database is migrated for each test
		flyway.clean();  // Optional: Resets the database to a clean state
		flyway.migrate();  // Apply migrations
	}

	@Test
	void testCreateProduct() {
		// Arrange
		Product product = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url",
				BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);

		// Act
		int rowsAffected = productRepository.create(product);

		// Assert
		assertEquals(1, rowsAffected);
		Product fetchedProduct = productRepository.getById(product.getId());
		assertNotNull(fetchedProduct);
		assertEquals(product.getName(), fetchedProduct.getName());
		assertEquals(product.getPrice(), fetchedProduct.getPrice());
	}

	@Test
	void testGetAllProductsWithCategory() {
		// Arrange
		Product product1 = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url",
				BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);
		Product product2 = new Product(UUID.randomUUID(), "Product 2", "Description", "image_url",
				BigDecimal.valueOf(20.99), ProductCategoryEnum.SIDE_DISH);
		productRepository.create(product1);
		productRepository.create(product2);

		// Act
		Product.ProductFilters filters = new Product.ProductFilters(ProductCategoryEnum.SANDWICH);
		List<Product> products = productRepository.getAll(filters);

		// Assert
		assertEquals(1, products.size());
		assertEquals(product1.getId(), products.get(0).getId());
	}

	@Test
	void testDeleteProduct() {
		// Arrange
		Product product = new Product(UUID.randomUUID(), "Product 1", "Description", "image_url",
				BigDecimal.valueOf(10.99), ProductCategoryEnum.SANDWICH);
		productRepository.create(product);

		// Act
		int rowsAffected = productRepository.delete(product.getId());

		// Assert
		assertEquals(1, rowsAffected);
		Product deletedProduct = productRepository.getById(product.getId());
		assertNull(deletedProduct);
	}
}
