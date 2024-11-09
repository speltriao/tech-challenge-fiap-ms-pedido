package com.fiap.techchallenge_order.domain.service;


import com.fiap.techchallenge_order.adapters.out.database.postgres.ProductRepository;
import com.fiap.techchallenge_order.domain.entity.Product;
import com.fiap.techchallenge_order.domain.repository.ProductRepositoryPort;
import com.fiap.techchallenge_order.domain.usecase.IProductUseCase;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;


public class ProductService implements IProductUseCase {

    private final ProductRepositoryPort productRepository;

    public ProductService(DataSource dataSource) {
        this.productRepository = new ProductRepository(dataSource);
    }

    @Override
    public Product createProduct(Product product)
    {
        product.setId(UUID.randomUUID());
        product.setPrice(formatToTwoDecimalPlaces(product.getPrice()));

        if(productRepository.create(product) == 1)
            return product;

        else return null;
    }

    @Override
    public List<Product> getAllProducts(Product.ProductFilters filters)
    {
        return productRepository.getAll(filters);
    }

    @Override
    public Boolean deleteProduct (String id)
    {
        var product = productRepository.getById(UUID.fromString(id));

        if(product == null)
            return false;

        int deleteFlag = productRepository.delete(UUID.fromString(id));
        return deleteFlag == 1;
    }

    private BigDecimal formatToTwoDecimalPlaces(BigDecimal input) {
        if (input == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}