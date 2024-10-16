package com.fiap.techchallenge.adapters.in.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fiap.techchallenge.adapters.in.rest.constants.FieldValidationConstants.NOT_BLANK;
import static com.fiap.techchallenge.adapters.in.rest.constants.FieldValidationConstants.NOT_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDTO {

    @NotBlank(message = NOT_BLANK)
    @Size(min = 36, max = 36, message = "must have 36 characters")
    private String id;

    @NotNull(message = NOT_NULL)
    @Min(value = 1, message = "must be greater than zero")
    @Max(value = Integer.MAX_VALUE, message = "is too long to be a integer")
    private Integer quantity;

}
