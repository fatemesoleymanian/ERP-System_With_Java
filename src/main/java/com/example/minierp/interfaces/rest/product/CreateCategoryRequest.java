package com.example.minierp.interfaces.rest.product;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "نام دسته بندی الزامیست.")
        String name
) {}
