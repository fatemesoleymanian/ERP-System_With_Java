package com.example.minierp.interfaces.rest.sales;

import com.example.minierp.domain.sales.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "پاسخ سفارش")
public record OrderResponse(
        @Schema(description = "شناسه سفارش")
        Long id,

        @Schema(description = "شماره سفارش")
        String orderNumber,

        @Schema(description = "تاریخ ایجاد")
        LocalDateTime createdAt,

        @Schema(description = "وضعیت سفارش")
        OrderStatus status,

        @Schema(description = "نسخه")
        Long version,

        @Schema(description = "آیتم‌های سفارش")
        List<OrderResponseItem> items,

        @Schema(description = "مبلغ کل بدون مالیات")
        BigDecimal subTotal,

        @Schema(description = "مبلغ مالیات")
        BigDecimal taxAmount,

        @Schema(description = "مبلغ نهایی")
        BigDecimal totalAmount,

        @Schema(description = "مقدار تخفیف سفارش")
        BigDecimal orderDiscountValue,

        @Schema(description = "درصد تخفیف سفارش")
        BigDecimal orderDiscountPercent,

        @Schema(description = "دلیل لغو")
        String cancelReason,

        @Schema(description = "نام مشتری")
        String customerName,

        @Schema(description = "بازه زمانی مورد نظر برای ارسال - شروع")
                LocalDateTime desiredDeliveryFrom,

        @Schema(description = "بازه زمانی مورد نظر برای ارسال - پایان")
        LocalDateTime desiredDeliveryTo
) {
    @Schema(description = "آیتم سفارش")
    public record OrderResponseItem(
            @Schema(description = "نام محصول")
            String productName,

            @Schema(description = "تعداد")
            Integer quantity,

            @Schema(description = "قیمت")
            BigDecimal price,

            @Schema(description = "مقدار تخفیف")
            BigDecimal discountValue,

            @Schema(description = "درصد تخفیف")
            BigDecimal discountPercent
    ) {}
}