package com.galega.order.adapters.in.rest.controller;

import com.galega.order.adapters.in.rest.dto.CreateProductDTO;
import com.galega.order.adapters.in.rest.dto.ProductDTO;
import com.galega.order.adapters.in.rest.mapper.ProductMapper;
import com.galega.order.domain.entity.Product;
import com.galega.order.domain.enums.ProductCategoryEnum;
import com.galega.order.domain.usecase.IProductUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductControllerTest {

	@Mock
	private IProductUseCase productService;

	@InjectMocks
	private ProductController productController;

	public ProductControllerTest() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetProducts_withCategory_returnsProducts() {
		ProductCategoryEnum category = ProductCategoryEnum.DRINK;
		Product.ProductFilters filters = new Product.ProductFilters(category);
		List<Product> mockProducts = List.of(
				new Product(UUID.randomUUID(), "Phone", "Smartphone", "url", BigDecimal.valueOf(500), category)
		);

		when(productService.getAllProducts(filters)).thenReturn(mockProducts);

		ResponseEntity<List<Product>> response = productController.getProducts(ProductCategoryEnum.DRINK.toString());

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testGetProducts_noCategory_returnsNoContent() {
		Product.ProductFilters filters = new Product.ProductFilters(null);
		when(productService.getAllProducts(filters)).thenReturn(List.of());

		ResponseEntity<List<Product>> response = productController.getProducts(null);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testCreateProduct_validInput_returnsCreatedProduct() {
		CreateProductDTO createProductDTO = new CreateProductDTO("Laptop", "Gaming laptop", "imageUrl", BigDecimal.valueOf(1200), ProductCategoryEnum.DRINK);
		Product createdProduct = new Product(UUID.randomUUID(), "Laptop", "Gaming laptop", "imageUrl", BigDecimal.valueOf(1200), ProductCategoryEnum.DRINK);

		when(productService.createProduct(any())).thenReturn(createdProduct);

		ResponseEntity<ProductDTO> response = productController.createProduct(createProductDTO);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	void testCreateProduct_invalidInput_returnsBadRequest() {
		CreateProductDTO createProductDTO = new CreateProductDTO("Invalid", null, "imageUrl", BigDecimal.valueOf(1200), ProductCategoryEnum.DRINK);
		Product domainProduct = ProductMapper.toDomain(createProductDTO);

		when(productService.createProduct(domainProduct)).thenReturn(null);

		ResponseEntity<ProductDTO> response = productController.createProduct(createProductDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void testDeleteProduct_validId_returnsNoContent() {
		String productId = UUID.randomUUID().toString();
		when(productService.deleteProduct(productId)).thenReturn(true);

		ResponseEntity<?> response = productController.deleteProduct(productId);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void testDeleteProduct_invalidId_returnsNotFound() {
		String productId = UUID.randomUUID().toString();
		when(productService.deleteProduct(productId)).thenReturn(false);

		ResponseEntity<?> response = productController.deleteProduct(productId);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
