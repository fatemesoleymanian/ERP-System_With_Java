package com.example.minierp.interfaces.rest.product;

import jakarta.validation.constraints.*;

public record CreateProductRequest(
        @NotBlank String  name,
        @NotBlank String sku,
        @NotNull @Positive double price,
        @NotNull @Min(0) int quantity
) {
}
