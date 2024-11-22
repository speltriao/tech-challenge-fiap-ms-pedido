package com.galega.order.domain.exception;


import com.galega.order.domain.enums.OrderStatusEnum;

import java.util.UUID;

public class OrderAlreadyWithStatusException extends Exception {

    public OrderAlreadyWithStatusException(UUID orderId, OrderStatusEnum status) {
        super("The order " + orderId.toString() + " is already with the status " + status.toString());
    }

}
