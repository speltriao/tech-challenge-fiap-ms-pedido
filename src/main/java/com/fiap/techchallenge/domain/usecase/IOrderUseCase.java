package com.fiap.techchallenge.domain.usecase;

import com.fiap.techchallenge.domain.entity.Order;
import com.fiap.techchallenge.domain.entity.OrderFilters;
import com.fiap.techchallenge.domain.entity.OrderHistory;
import com.fiap.techchallenge.domain.enums.OrderStatus;
import com.fiap.techchallenge.domain.exception.EntityNotFoundException;
import com.fiap.techchallenge.domain.exception.OrderAlreadyWithStatusException;


import java.util.List;
import java.util.UUID;

public interface IOrderUseCase {

    List<Order> getAll(OrderFilters filters);

    Order get(UUID id) throws EntityNotFoundException;

    Order create(Order dto) throws EntityNotFoundException;

    List<OrderHistory> getOrderHistory(UUID id) throws EntityNotFoundException;

    boolean updateStatus(UUID id, OrderStatus status) throws OrderAlreadyWithStatusException, EntityNotFoundException;
}
