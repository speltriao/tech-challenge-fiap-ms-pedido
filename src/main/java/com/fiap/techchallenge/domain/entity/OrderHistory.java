package com.fiap.techchallenge.domain.entity;

import com.fiap.techchallenge.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderHistory {

    private OrderStatus previousStatus;
    private OrderStatus lastStatus;
    private LocalDateTime moment;

    public OrderHistory() {}

    public OrderHistory(OrderStatus previousStatus, OrderStatus lastStatus, LocalDateTime moment) {
        this.previousStatus = previousStatus;
        this.lastStatus = lastStatus;
        this.moment = moment;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(OrderStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }
}
