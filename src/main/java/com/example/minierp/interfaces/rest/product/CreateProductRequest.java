package com.example.minierp.interfaces.rest.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank String  name,
        @NotBlank String sku,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) int quantity
) {
}
