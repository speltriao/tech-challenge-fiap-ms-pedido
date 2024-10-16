package com.fiap.techchallenge.domain.enums;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SortDirection")
public enum SortDirection {

    ASC("ASC"),
    DESC("DESC");

    private final String value;

    private SortDirection(String value){
        this.value = value;
    }

    public static SortDirection fromString(String value){
        if(value == null) return null;

        for(SortDirection sortDirection : values()){
            if(sortDirection.value.equals(value.toUpperCase()))
                return sortDirection;
        }

        return null;
    }

    public String toString() {
        return this.value;
    }

}
