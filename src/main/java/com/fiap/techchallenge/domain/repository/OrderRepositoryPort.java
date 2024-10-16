package com.fiap.techchallenge.domain.repository;

import com.fiap.techchallenge.domain.entity.Order;
import com.fiap.techchallenge.domain.entity.OrderFilters;
import com.fiap.techchallenge.domain.entity.OrderHistory;
import com.fiap.techchallenge.domain.enums.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepositoryPort {

    List<Order> getAll(OrderFilters filters);

    List<OrderHistory> getOrderHistoryByOrderId(UUID orderId);

    Order getById(UUID id);

    Order getByIdWithProducts(UUID id);

    int create(Order order);

    int updateStatus(Order order, OrderStatus newStatus, OrderStatus previousStatus);
}
