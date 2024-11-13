package com.galega.order.adapters.out.database.postgres.mapper;


import com.galega.order.domain.entity.Order;
import com.galega.order.domain.entity.OrderHistory;
import com.galega.order.domain.entity.Product;
import com.galega.order.domain.entity.ProductAndQuantity;
import com.galega.order.domain.enums.OrderStatus;
import com.galega.order.domain.enums.ProductCategory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class OrderMapper {

    public static RowMapper<Order> listMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            var order = createOrder(rs);
            order.setProducts(null);
            return order;
        }
    };

    public static RowMapper<Order> listWithProducts = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = createOrder(rs);
            List<ProductAndQuantity> products = new ArrayList<>();

            do {
                Product temp = new Product();
                int quantity = rs.getInt("quantity");
                temp.setId(UUID.fromString(rs.getString("product_id")));
                temp.setName(rs.getString("name"));
                temp.setCategory(ProductCategory.fromString(rs.getString("category")));
                temp.setPrice(rs.getBigDecimal("price"));
                temp.setDescription(rs.getString("description"));
                temp.setImageUrl(rs.getString("image_url"));
                products.add(new ProductAndQuantity(temp, quantity));
            } while (rs.next());

            order.setProducts(products);
            return order;
        }
    };

    public static RowMapper<OrderHistory> historyMapper = new RowMapper<OrderHistory>() {
        @Override
        public OrderHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderHistory record = new OrderHistory();
            record.setLastStatus(OrderStatus.fromString(rs.getString("new_status")));
            record.setPreviousStatus(OrderStatus.fromString(rs.getString("previous_status")));
            record.setMoment(rs.getTimestamp("moment").toLocalDateTime());

            return record;
        }
    };

    private static Order createOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(UUID.fromString(rs.getString("order_id")));
        order.setOrderNumber(rs.getInt("order_number"));
        order.setCustomerId(getUUID(rs.getString("customer_id")));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        if(rs.getTimestamp("paid_at") != null)
            order.setPaidAt(rs.getTimestamp("paid_at").toLocalDateTime());

        order.setAmount(rs.getBigDecimal("amount"));
        order.setStatus(OrderStatus.fromString(rs.getString("status")));
        return order;
    }

    private static UUID getUUID(String uuid) {
        if (StringUtils.hasText(uuid))
            return UUID.fromString(uuid);

        return null;
    }

}
