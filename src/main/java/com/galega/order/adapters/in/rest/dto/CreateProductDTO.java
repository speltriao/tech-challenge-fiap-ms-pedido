package com.galega.order.adapters.in.rest.dto;

import com.galega.order.domain.enums.ProductCategoryEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static com.galega.order.adapters.in.rest.constants.FieldValidationConstants.NOT_BLANK;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {

	@NotBlank(message = NOT_BLANK)
	String name;

	@NotBlank(message = NOT_BLANK)
	String description;

	@NotBlank(message = NOT_BLANK)
	String imageUrl;

	@DecimalMin(message = "must be greater than 0", value = "0", inclusive = false)
	BigDecimal price;

	ProductCategoryEnum category;
}