package com.fiap.techchallenge.adapters.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusDTO {

    @NotNull(message = "Field 'status' is mandatory")
    private String status;

}
