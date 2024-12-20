package com.galega.order.domain.service;

import com.galega.order.adapters.out.database.postgres.ProductRepository;
import com.galega.order.domain.entity.Product;
import com.galega.order.domain.repository.ProductRepositoryPort;
import com.galega.order.domain.usecase.IProductUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements IProductUseCase {

    @Autowired
    private ProductRepositoryPort productRepository;


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

    @Override
    public Product getById(UUID id){
        return productRepository.getById(id);
    }

    private BigDecimal formatToTwoDecimalPlaces(BigDecimal input) {
        if (input == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}