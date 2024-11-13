package com.galega.order.adapters.in.rest.mapper;

import com.galega.order.adapters.in.rest.dto.CreateProductDTO;
import com.galega.order.adapters.in.rest.dto.ProductDTO;
import com.galega.order.domain.entity.Product;

public abstract class ProductMapper {

  public static Product toDomain(CreateProductDTO dto) {
    Product product = new Product();
    product.setName(dto.getName());
    product.setDescription(dto.getDescription());
    product.setImageUrl(dto.getImageUrl());
    product.setPrice(dto.getPrice());
    product.setCategory(dto.getCategory());
    return product;
  }

  public static ProductDTO toDto(Product product) {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setId(product.getId());
    productDTO.setName(product.getName());
    productDTO.setDescription(product.getDescription());
    productDTO.setImageUrl(product.getImageUrl());
    productDTO.setPrice(product.getPrice());
    productDTO.setCategory(product.getCategory());
    return productDTO;
  }

}
