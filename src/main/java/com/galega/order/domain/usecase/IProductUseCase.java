package com.galega.order.domain.usecase;

import com.galega.order.domain.entity.Product;

import java.util.List;
import java.util.UUID;

public interface IProductUseCase {

    Product createProduct(Product product);
    Product getById(UUID id);

    List<Product> getAllProducts(Product.ProductFilters filters);

    Boolean deleteProduct(String id);
}
