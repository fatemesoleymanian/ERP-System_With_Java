package com.example.minierp.interfaces.rest.sales;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "شناسه مشتری الزامی است")
        Long customerId,

        @NotEmpty(message = "سفارش باید حداقل یک آیتم داشته باشد")
        @Valid
        List<CreateOrderItem> items,

        @DecimalMin(value = "0.0", message = "تخفیف سفارش نمی‌تواند منفی باشد")
        BigDecimal orderDiscountValue,

        @DecimalMin(value = "0.0", message = "درصد تخفیف سفارش نمی‌تواند منفی باشد")
        @DecimalMax(value = "100.0", message = "درصد تخفیف سفارش نمی‌تواند بیشتر از ۱۰۰ باشد")
        BigDecimal orderDiscountPercent,

        LocalDateTime desiredDeliveryFrom,
        LocalDateTime desiredDeliveryTo
) {
        public record CreateOrderItem(
                @NotNull(message = "شناسه محصول الزامی است")
                Long productId,

                @Min(value = 1, message = "حداقل تعداد ۱ است")
                @Max(value = 50, message = "حداکثر تعداد ۵۰ است")
                int quantity,

                @DecimalMin(value = "0.0", message = "مقدار تخفیف نمی‌تواند منفی باشد")
                BigDecimal discountValue,

                @DecimalMin(value = "0.0", message = "درصد تخفیف نمی‌تواند منفی باشد")
                @DecimalMax(value = "100.0", message = "درصد تخفیف نمی‌تواند بیشتر از ۱۰۰ باشد")
                BigDecimal discountPercent
        ) { }
}
