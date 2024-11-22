package com.galega.order.domain.enums;

import org.springframework.util.StringUtils;

public enum ProductCategoryEnum {

    SANDWICH ("SANDWICH"),
    SIDE_DISH ("SIDE_DISH"),
    DRINK ("DRINK"),
    DESSERT ("DESSERT");

    private final String value;

    private ProductCategoryEnum(String status){
        this.value = status;
    }

    public static ProductCategoryEnum fromString(String status){
        if(!StringUtils.hasText(status)) return null;

        for(ProductCategoryEnum category : values()){
            if(category.value.equals(status))
                return category;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
