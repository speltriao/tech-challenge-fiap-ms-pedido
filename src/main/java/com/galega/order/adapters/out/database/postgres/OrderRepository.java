package com.galega.order.adapters.out.database.postgres;


import com.galega.order.adapters.out.database.postgres.mapper.OrderMapper;
import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderFilters;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.entity.ProductAndQuantity;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.repository.OrderRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository("OrderRepository")
public class OrderRepository implements OrderRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Order> getAll(OrderFilters filters) {
        String sql = "SELECT *, id as order_id FROM public.order";

        if (filters != null){
            if(filters.getStatus() != null) {
                sql += " WHERE status = '" + filters.getStatus() + "'";
            }

            if(filters.getOrderBy() != null) {
                sql += " ORDER BY " + filters.getOrderBy().toString().toLowerCase() + " " + filters.getDirection();
            }
        }

        return jdbcTemplate.query(sql, OrderMapper.listMapper);
    }

    @Override
    public List<OrderHistory> getOrderHistoryByOrderId(UUID orderId) {
        String sql = "SELECT * " +
                "FROM public.order_history " +
                "WHERE order_id = ?";

        return jdbcTemplate.query(sql, OrderMapper.historyMapper, orderId);
    }

    @Override
    public Order getByIdWithProducts(UUID id) {
        String sql = "SELECT op.order_id, op.product_id, o.amount, o.created_at, o.paid_at, o.status, o.order_number, op.quantity, o.customer_id, p.name, p.price, p.category, p.description, p.image_url " +
                "FROM public.order o " +
                "LEFT JOIN public.order_products op ON op.order_id = o.id " +
                "LEFT JOIN public.product p ON p.id = op.product_id " +
                "WHERE o.id = ?";

        var data = jdbcTemplate.query(sql, OrderMapper.listWithProducts, id);
        if(data.isEmpty()) return null;

        return data.getFirst();
    }

    @Override
    public int create(Order order) {
        String createOrderSQL = "INSERT INTO public.order " +
                "(id, customer_id, created_at, amount, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        String createOrderProductRelationSQL = "INSERT INTO public.order_products " +
                "(order_id, product_id, quantity) " +
                "VALUES (?,?,?)";

        // Storage in Order Table
        int isOrderCreated = jdbcTemplate.update(
            createOrderSQL,
            order.getId(),
            order.getCustomerId(),
            order.getCreatedAt(),
            order.getAmount(),
            order.getStatus().toString()
        );

        // Insert in the Relational Table
        for(ProductAndQuantity product : order.getProducts()) {
             jdbcTemplate.update(
                createOrderProductRelationSQL,
                order.getId(),
                product.getProduct().getId(),
                product.getQuantity()
            );
        }

        return isOrderCreated;

    }

    @Override
    public int updateStatus(Order order, OrderStatusEnum newStatus, OrderStatusEnum previousStatus) {
        LocalDateTime now = LocalDateTime.now();
        String updateStatusSQL = "UPDATE public.order SET status = ?, paid_at = ? WHERE id = ?";
        String relationInsertSQL = "INSERT INTO public.order_history (order_id, previous_status, new_status, moment) VALUES (?, ?, ?, ?)";

        int updated = jdbcTemplate.update(
            updateStatusSQL,
            newStatus.toString(),
            order.getPaidAt(),
            order.getId()
        );

        int relationInsertResult = jdbcTemplate.update(
            relationInsertSQL,
            order.getId(),
            previousStatus.toString(),
            newStatus.toString(),
            now
        );

        return updated + relationInsertResult;
    }

    @Override
    public Order getById(UUID id) {
        String sql = "SELECT *, id as order_id " +
                "FROM public.order o " +
                "WHERE id = ?";

        var data = jdbcTemplate.query(sql, OrderMapper.listMapper, id);
        if(data.isEmpty()) return null;

        return data.getFirst();
    }

}
