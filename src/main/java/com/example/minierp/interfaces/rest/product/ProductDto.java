package com.example.minierp.interfaces.rest.product;

public record ProductDto (
        long id,
        String name,
        String sku,
        double price,
        int quantity
){}




