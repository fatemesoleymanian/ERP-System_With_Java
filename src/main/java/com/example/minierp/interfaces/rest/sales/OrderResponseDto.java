package com.example.minierp.interfaces.rest.sales;

import com.example.minierp.domain.sales.OrderItem;
import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.domain.sales.SaleOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "پاسخ سفارش فروش")
public record OrderResponseDto(

        @Schema(description = "شناسه سفارش")
        Long id,

        @Schema(description = "شماره سفارش (کد یکتای سفارش)")
        String orderNumber,

        @Schema(description = "وضعیت سفارش (مثلاً در حال پردازش، ارسال‌شده، لغوشده و غیره)")
        OrderStatus status,

        @Schema(description = "جمع مبلغ اقلام قبل از مالیات")
        BigDecimal subTotal,

        @Schema(description = "مبلغ مالیات اعمال‌شده بر سفارش")
        BigDecimal taxAmount,

        @Schema(description = "مبلغ نهایی سفارش پس از تخفیف و مالیات")
        BigDecimal totalAmount,

        @Schema(description = "مقدار تخفیف ثابت سفارش")
        BigDecimal orderDiscountValue,

        @Schema(description = "درصد تخفیف سفارش")
        BigDecimal orderDiscountPercent,

        @Schema(description = "دلیل لغو سفارش (در صورت لغو شدن)")
        String cancelReason,

        @Schema(description = "تاریخ شروع بازه‌ی مورد نظر برای تحویل سفارش")
        LocalDateTime desiredDeliveryFrom,

        @Schema(description = "تاریخ پایان بازه‌ی مورد نظر برای تحویل سفارش")
        LocalDateTime desiredDeliveryTo,

        @Schema(description = "شناسه مشتری مربوط به سفارش")
        Long customerId,

        @Schema(description = "نام مشتری مربوط به سفارش")
        String customerName,

        @Schema(description = "لیست اقلام موجود در سفارش")
        List<OrderItemResponse> items,

        @Schema(description = "تاریخ ایجاد سفارش")
        LocalDateTime createdAt,

        @Schema(description = "تاریخ آخرین به‌روزرسانی سفارش")
        LocalDateTime updatedAt
) {

        public static OrderResponseDto fromEntity(SaleOrder order) {
                return new OrderResponseDto(
                        order.getId(),
                        order.getOrderNumber(),
                        order.getStatus(),
                        order.getSubTotal(),
                        order.getTaxAmount(),
                        order.getTotalAmount(),
                        order.getOrderDiscountValue(),
                        order.getOrderDiscountPercent(),
                        order.getCancelReason(),
                        order.getDesiredDeliveryFrom(),
                        order.getDesiredDeliveryTo(),
                        order.getCustomer().getId(),
                        order.getCustomer().getName(),
                        order.getItems().stream().map(OrderItemResponse::fromEntity).toList(),
                        order.getCreatedAt(),
                        order.getUpdatedAt()
                );
        }

        @Schema(description = "اقلام سفارش")
        public record OrderItemResponse(

                @Schema(description = "شناسه قلم سفارش")
                Long id,

                @Schema(description = "شناسه محصول")
                Long productId,

                @Schema(description = "نام محصول")
                String productName,

                @Schema(description = "تعداد سفارش داده‌شده از این محصول")
                int quantity,

                @Schema(description = "قیمت واحد محصول در زمان سفارش")
                BigDecimal price,

                @Schema(description = "مقدار تخفیف ثابت برای این قلم")
                BigDecimal discountValue,

                @Schema(description = "درصد تخفیف برای این قلم")
                BigDecimal discountPercent
        ) {
                public static OrderItemResponse fromEntity(OrderItem item) {
                        return new OrderItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getDiscountValue(),
                                item.getDiscountPercent()
                        );
                }
        }
}
