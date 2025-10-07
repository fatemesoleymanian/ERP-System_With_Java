package com.example.minierp.interfaces.rest.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "پاسخ دسته بندی")
public record CategoryResponse(
        @Schema(description = "شناسه دسته بندی")
        Long id,

        @Schema(description = "نام دسته بندی")
        String name
) {}
