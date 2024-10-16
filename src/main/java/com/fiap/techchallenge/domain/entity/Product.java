package com.fiap.techchallenge.domain.entity;


import com.fiap.techchallenge.domain.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {

    private UUID id;

    private String name;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    private ProductCategory category;

    public Product(UUID id) {
        this.id = id;
    }

    public Product(UUID id, String name, String description, String imageUrl, BigDecimal price, ProductCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
    }

    public Product() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public static class ProductFilters {

        private ProductCategory category;

        public ProductFilters(ProductCategory category) {
            this.category = category;
        }

        public ProductFilters() {}

        public ProductCategory getCategory() {
            return category;
        }

        public void setCategory(ProductCategory category) {
            this.category = category;
        }
    }

}
