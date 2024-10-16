package com.fiap.techchallenge.domain.enums;

public enum OrderStatus {

    CREATED ("CREATED"),
    RECEIVED ("RECEIVED"),
    IN_PREPARATION ("IN_PREPARATION"),
    READY_TO_DELIVERY ("READY_TO_DELIVERY"),
    CANCELED ("CANCELED"),
    FINISHED ("FINISHED");

    private final String status;

    private OrderStatus(String status){
        this.status = status;
    }

    public static OrderStatus fromString(String status){
        if(status == null) return null;

        for(OrderStatus orderStatus : values()){
            if(orderStatus.status.equals(status.toUpperCase()))
                return orderStatus;
        }

        return null;
    }

    public String toString() {
        return this.status;
    }

    public boolean isDefaultListStatus() {
        return this.status.equals(READY_TO_DELIVERY.toString())
            || this.status.equals(IN_PREPARATION.toString())
            || this.status.equals(RECEIVED.toString());
    }
}
