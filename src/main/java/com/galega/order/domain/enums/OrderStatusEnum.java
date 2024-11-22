package com.galega.order.domain.enums;

public enum OrderStatusEnum {

    CREATED ("CREATED"), //PEDIDO RECEBIDO
    RECEIVED ("RECEIVED"), //PAGO
    IN_PREPARATION ("IN_PREPARATION"),
    READY_TO_DELIVERY ("READY_TO_DELIVERY"),
    CANCELED ("CANCELED"),
    FINISHED ("FINISHED");

    private final String status;

    OrderStatusEnum(String status){
        this.status = status;
    }

    public static OrderStatusEnum fromString(String status){
        if(status == null) return null;

        for(OrderStatusEnum orderStatusEnum : values()){
            if(orderStatusEnum.status.equals(status.toUpperCase()))
                return orderStatusEnum;
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
