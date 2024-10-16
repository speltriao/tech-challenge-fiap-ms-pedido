package com.fiap.techchallenge.domain.enums;

public enum OrderSortFields {

    CREATED_AT ("CREATED_AT"),
    ORDER_NUMBER ("ORDER_NUMBER"),
    AMOUNT ("AMOUNT");

    private final String value;

    private OrderSortFields(String value){
        this.value = value;
    }

    public static OrderSortFields fromString(String value){
        if(value == null) return null;

        for(OrderSortFields field : values()){
            if(field.value.equals(value.toUpperCase()))
                return field;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
