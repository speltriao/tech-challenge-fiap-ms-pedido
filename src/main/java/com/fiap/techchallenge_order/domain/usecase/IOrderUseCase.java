package com.fiap.techchallenge_order.domain.usecase;

import com.fiap.techchallenge_order.domain.entity.Order;
import com.fiap.techchallenge_order.domain.entity.OrderFilters;
import com.fiap.techchallenge_order.domain.entity.OrderHistory;
import com.fiap.techchallenge_order.domain.enums.OrderStatus;
import com.fiap.techchallenge_order.domain.exception.EntityNotFoundException;
import com.fiap.techchallenge_order.domain.exception.OrderAlreadyWithStatusException;


import java.util.List;
import java.util.UUID;

public interface IOrderUseCase {

    List<Order> getAll(OrderFilters filters);

    List<Order> getDefaultListOrders();

    Order get(UUID id) throws EntityNotFoundException;

    Order create(Order dto) throws IllegalArgumentException;

    List<OrderHistory> getOrderHistory(UUID id) throws EntityNotFoundException;

    boolean updateStatus(UUID id, OrderStatus status) throws OrderAlreadyWithStatusException, EntityNotFoundException;
}
