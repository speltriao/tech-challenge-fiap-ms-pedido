package com.galega.order.domain.enums;

public enum OrderSortFieldsEnum {

    CREATED_AT ("CREATED_AT"),
    ORDER_NUMBER ("ORDER_NUMBER"),
    AMOUNT ("AMOUNT");

    private final String value;

    private OrderSortFieldsEnum(String value){
        this.value = value;
    }

    public static OrderSortFieldsEnum fromString(String value){
        if(value == null) return null;

        for(OrderSortFieldsEnum field : values()){
            if(field.value.equals(value.toUpperCase()))
                return field;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
