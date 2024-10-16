package com.fiap.techchallenge.domain.entity;

import com.fiap.techchallenge.domain.enums.OrderSortFields;
import com.fiap.techchallenge.domain.enums.OrderStatus;
import com.fiap.techchallenge.domain.enums.SortDirection;

public class OrderFilters {

    private OrderStatus status;
    private OrderSortFields orderBy;
    private SortDirection direction;

    public OrderFilters() {}

    public OrderFilters(OrderStatus status, OrderSortFields orderBy, SortDirection direction) {
        this.status = status;
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderSortFields getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderSortFields orderBy) {
        this.orderBy = orderBy;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortDirection direction) {
        this.direction = direction;
    }

    public boolean hasNoParameters() {
        return status == null && orderBy == null && direction == null;
    }

}