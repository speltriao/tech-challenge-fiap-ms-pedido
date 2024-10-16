package com.fiap.techchallenge.domain.entity;

import com.fiap.techchallenge.domain.enums.OrderStatus;
import com.fiap.techchallenge.domain.payment.Payment;
import com.fiap.techchallenge.domain.product.ProductAndQuantity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {

    private UUID id;

    private UUID customerId;

    private Integer orderNumber;

    private BigDecimal amount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private long waitingTimeInSeconds;

    private List<ProductAndQuantity> products;

    private List<OrderHistory> history;

    private Payment payment;

    public Order() {}

    public Order(UUID id, UUID customerId, Integer orderNumber, BigDecimal amount, OrderStatus status, LocalDateTime createdAt, long waitingTimeInSeconds, List<ProductAndQuantity> products, List<OrderHistory> history, LocalDateTime paidAt) {
        this.id = id;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.waitingTimeInSeconds = waitingTimeInSeconds;
        this.products = products;
        this.history = history;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getWaitingTimeInSeconds() {
        return waitingTimeInSeconds;
    }

    public void setWaitingTimeInSeconds(long waitingTimeInSeconds) {
        this.waitingTimeInSeconds = waitingTimeInSeconds;
    }

    public List<ProductAndQuantity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductAndQuantity> products) {
        this.products = products;
    }

    public List<OrderHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OrderHistory> history) {
        this.history = history;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
