package com.galega.order.domain.repository;



import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.enums.OrderStatusEnum;
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

    int updateStatus(Order order, OrderStatusEnum newStatus, OrderStatusEnum previousStatus);
}
