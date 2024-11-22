package com.galega.order.domain.enums;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SortDirection")
public enum SortDirectionEnum {

    ASC("ASC"),
    DESC("DESC");

    private final String value;

    private SortDirectionEnum(String value){
        this.value = value;
    }

    public static SortDirectionEnum fromString(String value){
        if(value == null) return null;

        for(SortDirectionEnum sortDirectionEnum : values()){
            if(sortDirectionEnum.value.equals(value.toUpperCase()))
                return sortDirectionEnum;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
