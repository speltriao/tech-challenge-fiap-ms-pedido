package com.fiap.techchallenge.domain.exception;

import com.fiap.techchallenge.domain.enums.OrderStatus;

import java.util.UUID;

public class OrderAlreadyWithStatusException extends Exception {

    public OrderAlreadyWithStatusException(UUID orderId, OrderStatus status) {
        super("The order " + orderId.toString() + " is already with the status " + status.toString());
    }

}
