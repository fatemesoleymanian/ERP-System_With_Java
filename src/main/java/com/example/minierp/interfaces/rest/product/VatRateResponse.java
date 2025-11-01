package com.example.minierp.interfaces.rest.product;

import com.example.minierp.domain.product.VatRate;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "پاسخ مربوط به نرخ مالیات بر ارزش افزوده (VAT Rate)")
public record VatRateResponse(
        @Schema(description = "شناسه یکتای نرخ مالیات")
        Long id,
        @Schema(description = "نام نرخ مالیات")
        String name,
        @Schema(description = "درصد مالیات (نرخ VAT)")
        BigDecimal rate,
        @Schema(description = "آیا این نرخ به عنوان نرخ پیش‌فرض استفاده می‌شود؟")
        boolean isDefault
) {
    public static VatRateResponse fromEntity(VatRate vatRate) {
        return new VatRateResponse(
                vatRate.getId(),
                vatRate.getName(),
                vatRate.getRate(),
                vatRate.isDefault()
        );
    }
}
