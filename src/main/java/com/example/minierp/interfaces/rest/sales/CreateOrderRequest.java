package com.example.minierp.interfaces.rest.sales;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "شناسه مشتری الزامی است")
        @Positive(message = "شناسه مشتری باید مثبت باشد")
        Long customerId,

        @NotEmpty(message = "سفارش باید حداقل یک آیتم داشته باشد")
        @Size(max = 20, message = "سفارش نمی‌تواند بیش از ۲۰ آیتم داشته باشد")
        List<@Valid CreateOrderItem> items,

        @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
        BigDecimal orderDiscountValue,

        @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
        @DecimalMax(value = "100.0", message = "تخفیف نمی‌تواند بزرگتر از صد باشد")
        BigDecimal orderDiscountPercent,

        LocalDateTime desiredDeliveryFrom,
        LocalDateTime desiredDeliveryTo
) {
    @Schema(name = "CreateOrderItem")
    public record CreateOrderItem(
            @NotNull(message = "محصول باید انتخاب شود")
            @Positive(message = "شناسه محصول باید مثبت باشد")
            Long productId,

            @Min(value = 1, message = "حداقل تعداد سفارش ۱ است")
            @Max(value = 50, message = "حداکثر تعداد سفارش ۵۰ است")
            int quantity,

            @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
            BigDecimal discountValue,

            @DecimalMin(value = "0.0", message = "تخفیف نمی‌تواند منفی باشد")
            @DecimalMax(value = "100.0", message = "تخفیف نمی‌تواند بزرگتر از صد باشد")
            BigDecimal discountPercent
    ) {}
}