package com.galega.order.domain.service;

import com.galega.order.domain.entity.Product;
import com.galega.order.domain.entity.Product.ProductFilters;
import com.galega.order.domain.repository.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

	@Mock
	private ProductRepositoryPort productRepository;

	@InjectMocks
	private ProductService productService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createProduct_ShouldReturnCreatedProduct_WhenRepositoryReturnsSuccess() {
		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(10.567));
		product.setName("Test Product");

		when(productRepository.create(any(Product.class))).thenReturn(1);

		Product createdProduct = productService.createProduct(product);

		assertNotNull(createdProduct);
		assertNotNull(createdProduct.getId());
		assertEquals(BigDecimal.valueOf(10.57), createdProduct.getPrice());
		verify(productRepository).create(any(Product.class));
	}

	@Test
	void createProduct_ShouldReturnNull_WhenRepositoryFails() {
		Product product = new Product();
		product.setPrice(BigDecimal.valueOf(10.50));

		when(productRepository.create(any(Product.class))).thenReturn(0);

		Product createdProduct = productService.createProduct(product);

		assertNull(createdProduct);
		verify(productRepository).create(any(Product.class));
	}

	@Test
	void getAllProducts_ShouldReturnProducts_WhenFiltersAreProvided() {
		ProductFilters filters = new ProductFilters();
		Product product = new Product(UUID.randomUUID(), "Test Product", "Description", "image.jpg", BigDecimal.valueOf(10.50), null);

		when(productRepository.getAll(filters)).thenReturn(Collections.singletonList(product));

		List<Product> products = productService.getAllProducts(filters);

		assertNotNull(products);
		assertEquals(1, products.size());
		assertEquals("Test Product", products.get(0).getName());
		verify(productRepository).getAll(filters);
	}

	@Test
	void deleteProduct_ShouldReturnTrue_WhenProductIsDeleted() {
		UUID productId = UUID.randomUUID();
		Product product = new Product(productId);

		when(productRepository.getById(productId)).thenReturn(product);
		when(productRepository.delete(productId)).thenReturn(1);

		boolean isDeleted = productService.deleteProduct(productId.toString());

		assertTrue(isDeleted);
		verify(productRepository).getById(productId);
		verify(productRepository).delete(productId);
	}

	@Test
	void deleteProduct_ShouldReturnFalse_WhenProductDoesNotExist() {
		UUID productId = UUID.randomUUID();

		when(productRepository.getById(productId)).thenReturn(null);

		boolean isDeleted = productService.deleteProduct(productId.toString());

		assertFalse(isDeleted);
		verify(productRepository).getById(productId);
		verify(productRepository, never()).delete(productId);
	}

	@Test
	void formatToTwoDecimalPlaces_ShouldThrowException_WhenPriceIsNull() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			productService.createProduct(new Product(null, "name", "desc", "url", null, null));
		});

		assertEquals("Price cannot be null", exception.getMessage());
	}
}
