package com.example.minierp.interfaces.rest.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record VatRateRequest(
        @NotBlank(message = "نام نرخ مالیات الزامی است")
        String name,

        @NotNull(message = "نرخ مالیات الزامی است")
        @DecimalMin(value = "0.0", message = "نرخ مالیات نمی‌تواند منفی باشد")
        BigDecimal rate,

        boolean isDefault
) {}
