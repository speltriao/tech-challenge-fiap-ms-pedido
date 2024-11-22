package com.galega.order.domain.usecase;

import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.enums.PaymentStatusEnum;
import com.galega.order.domain.exception.EntityNotFoundException;
import com.galega.order.domain.exception.OrderAlreadyWithStatusException;

import java.util.List;
import java.util.UUID;

public interface IOrderUseCase {

    List<Order> getAll(OrderFilters filters);

    List<Order> getDefaultListOrders();

    Order get(UUID id) throws EntityNotFoundException;

    Order create(Order dto) throws IllegalArgumentException;

    List<OrderHistory> getOrderHistory(UUID id) throws EntityNotFoundException;

    boolean updateStatus(UUID id, OrderStatusEnum status) throws OrderAlreadyWithStatusException, EntityNotFoundException;
    boolean processOrderPayment(UUID orderId, PaymentStatusEnum paymentStatusEnum) throws OrderAlreadyWithStatusException, EntityNotFoundException;
}
