package com.fiap.techchallenge.domain.entity;

public class ProductAndQuantity {

    private Product product;
    private int quantity;

    public ProductAndQuantity() {}

    public ProductAndQuantity(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
