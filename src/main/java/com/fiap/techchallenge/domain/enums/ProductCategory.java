package com.fiap.techchallenge.domain.enums;

import org.springframework.util.StringUtils;

public enum ProductCategory {

    SANDWICH ("SANDWICH"),
    SIDE_DISH ("SIDE_DISH"),
    DRINK ("DRINK"),
    DESSERT ("DESSERT");

    private final String value;

    private ProductCategory(String status){
        this.value = status;
    }

    public static ProductCategory fromString(String status){
        if(!StringUtils.hasText(status)) return null;

        for(ProductCategory category : values()){
            if(category.value.equals(status))
                return category;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
