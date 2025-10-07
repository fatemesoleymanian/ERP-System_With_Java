package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "تراکنش موجودی (ورود و خروج کالا در انبار)")
public record InventoryTransactionDto(
        @Schema(description = "شناسه تراکنش موجودی")
        @NotNull
        Long id,

        @Schema(description = "تاریخ و زمان ثبت تراکنش")
        @NotNull
        LocalDateTime createdAt,

        @Schema(description = "نوع تراکنش موجودی (ورود، خروج و غیره)")
        @NotNull
        InventoryTransactionType type,

        @Schema(description = "تعداد کالای مربوط به این تراکنش")
        @NotNull
        int quantity,

        @Schema(description = "موجودی لحظه‌ای پس از انجام این تراکنش")
        @NotNull
        int runningBalance,

        @Schema(description = "شناسه سفارش مرتبط با این تراکنش (در صورت وجود)")
        @NotNull
        Long orderId,

        @Schema(description = "شناسه محصول مربوط به این تراکنش")
        @NotNull
        Long productId
) {}


