package com.fiap.techchallenge_order.adapters.in.rest.controller;


import com.fiap.techchallenge_order.adapters.in.rest.dto.CreateProductDTO;
import com.fiap.techchallenge_order.adapters.in.rest.dto.ProductDTO;
import com.fiap.techchallenge_order.adapters.in.rest.mapper.ProductMapper;
import com.fiap.techchallenge_order.domain.entity.Product;
import com.fiap.techchallenge_order.domain.enums.ProductCategory;
import com.fiap.techchallenge_order.domain.service.ProductService;
import com.fiap.techchallenge_order.domain.usecase.IProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

@Tag(name = "Product Controller")
@RestController
@RequestMapping("/products")
public class ProductController {

  private final IProductUseCase productService;

  public ProductController(DataSource dataSource) {
    this.productService = new ProductService(dataSource);
  }

  @GetMapping
    @Operation(
        summary = "List all storage products",
        parameters = {@Parameter(name = "category", schema = @Schema(implementation = ProductCategory.class))}
    )
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category)
    {

        ProductCategory productCategory = category != null
                ? ProductCategory.valueOf(category.toUpperCase())
                : null;

        Product.ProductFilters filters = new Product.ProductFilters(productCategory);
        var products = productService.getAllProducts(filters);

        if(products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Creates a new product")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid CreateProductDTO createProductDTO)
    {

        Product product = ProductMapper.toDomain(createProductDTO);
        Product createdProduct = productService.createProduct(product);

        if(createdProduct != null){
            ProductDTO response = ProductMapper.toDto(createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Delete a product from database")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct (@PathVariable String id)
    {
        if(productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
