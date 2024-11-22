package com.galega.order.domain.entity;


import com.galega.order.domain.enums.OrderStatusEnum;

import java.time.LocalDateTime;

public class OrderHistory {

    private OrderStatusEnum previousStatus;
    private OrderStatusEnum lastStatus;
    private LocalDateTime moment;

    public OrderHistory() {}

    public OrderHistory(OrderStatusEnum previousStatus, OrderStatusEnum lastStatus, LocalDateTime moment) {
        this.previousStatus = previousStatus;
        this.lastStatus = lastStatus;
        this.moment = moment;
    }

    public OrderStatusEnum getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatusEnum previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OrderStatusEnum getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(OrderStatusEnum lastStatus) {
        this.lastStatus = lastStatus;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public void setMoment(LocalDateTime moment) {
        this.moment = moment;
    }
}
