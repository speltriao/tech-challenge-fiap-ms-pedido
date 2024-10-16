package com.fiap.techchallenge.adapters.in.rest.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {

    @Nullable
    @Size(min = 36, max = 36)
    private String customerId;

    @Valid
    private List<OrderProductDTO> products;

}
