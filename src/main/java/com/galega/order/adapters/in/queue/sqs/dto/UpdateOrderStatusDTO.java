package com.galega.order.adapters.in.queue.sqs.dto;

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

    @NotNull(message = "Field 'ID' is mandatory")
    private String id;

}
