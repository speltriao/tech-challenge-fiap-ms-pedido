package com.fiap.techchallenge.adapters.in.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private String message;
    private String details;

    public ErrorDTO(String message) {
        this.message = message;
    }

}
