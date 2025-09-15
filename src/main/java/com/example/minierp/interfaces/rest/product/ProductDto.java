package com.example.minierp.interfaces.rest.product;

import java.math.BigDecimal;

public record ProductDto (
        long id,
        String name,
        String sku,
        BigDecimal price,
        int quantity
){}




