package com.example.minierp.interfaces.rest.product;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "نام محصول الزامی است")
        @Size(max = 100, message = "نام محصول نمی‌تواند بیشتر از ۱۰۰ کاراکتر باشد")
        String name,

        @NotBlank(message = "کد SKU الزامی است")
        @Size(max = 50, message = "کد SKU نمی‌تواند بیشتر از ۵۰ کاراکتر باشد")
        String sku,

        @NotNull(message = "دسته بندی محصول الزامی است")
        @Positive(message = "شناسه دسته‌بندی باید مثبت باشد")
        Long categoryId,

        @NotNull(message = "نرخ مالیات الزامی است")
        @Positive(message = "شناسه نرخ مالیات باید مثبت باشد")
        Long vatRateId,
        @NotNull(message = "قیمت محصول الزامی است")
        @DecimalMin(value = "0.0", message = "قیمت نمی‌تواند منفی باشد")
        BigDecimal price,

        @NotNull(message = "تعداد موجودی الزامی است")
        @Min(value = 0, message = "تعداد نمی‌تواند منفی باشد")
        int quantity,

        @DecimalMin(value = "0.0", message = "مقدار تخفیف نمی‌تواند منفی باشد")
        BigDecimal discountValue,

        @DecimalMin(value = "0.0", message = "درصد تخفیف نمی‌تواند منفی باشد")
        @DecimalMax(value = "100.0", message = "درصد تخفیف نمی‌تواند بیشتر از ۱۰۰ باشد")
        Double discountPercentage
) {}
