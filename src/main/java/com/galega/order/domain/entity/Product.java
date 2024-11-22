package com.galega.order.domain.entity;


import com.galega.order.domain.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {

    private UUID id;

    private String name;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    private ProductCategoryEnum category;

    public Product(UUID id) {
        this.id = id;
    }

    public Product(UUID id, String name, String description, String imageUrl, BigDecimal price, ProductCategoryEnum category) {
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

    public ProductCategoryEnum getCategory() {
        return category;
    }

    public void setCategory(ProductCategoryEnum category) {
        this.category = category;
    }

    public static class ProductFilters {

        private ProductCategoryEnum category;

        public ProductFilters(ProductCategoryEnum category) {
            this.category = category;
        }

        public ProductFilters() {}

        public ProductCategoryEnum getCategory() {
            return category;
        }

        public void setCategory(ProductCategoryEnum category) {
            this.category = category;
        }
    }

}
