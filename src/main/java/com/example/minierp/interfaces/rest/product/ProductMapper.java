package com.example.minierp.interfaces.rest.product;

import com.example.minierp.domain.product.Product;

public class ProductMapper {

    public static ProductDto toDto(Product product){
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getQuantity()
        );
    }
    public static Product toEntity(CreateProductRequest request){
        return Product.builder()
                .name(request.name())
                .sku(request.sku())
                .price(request.price())
                .quantity(request.quantity())
                .build();
    }
}
