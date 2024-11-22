package com.galega.order.domain.entity;


import com.galega.order.domain.enums.OrderSortFieldsEnum;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.SortDirectionEnum;

public class OrderFilters {

    private OrderStatusEnum status;
    private OrderSortFieldsEnum orderBy;
    private SortDirectionEnum direction;

    public OrderFilters() {}

    public OrderFilters(OrderStatusEnum status, OrderSortFieldsEnum orderBy, SortDirectionEnum direction) {
        this.status = status;
        this.orderBy = orderBy;
        this.direction = direction;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public OrderSortFieldsEnum getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderSortFieldsEnum orderBy) {
        this.orderBy = orderBy;
    }

    public SortDirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(SortDirectionEnum direction) {
        this.direction = direction;
    }

    public boolean hasNoParameters() {
        return status == null && orderBy == null && direction == null;
    }

}