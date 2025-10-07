package com.example.minierp.interfaces.rest.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "پاسخ محصول")
public record ProductResponse(

        @Schema(description = "شناسه محصول")
        Long id,

        @Schema(description = "نام محصول")
        String name,

        @Schema(description = "کد SKU محصول (کد یکتای شناسایی در انبار یا سیستم)")
        String sku,

        @Schema(description = "دسته بندی محصول")
        String categoryName,


        @Schema(description = "قیمت محصول")
        BigDecimal price,

        @Schema(description = "تعداد موجودی محصول در انبار")
        int quantity,

        @Schema(description = "مقدار تخفیف ثابت برای محصول")
        BigDecimal discountValue,

        @Schema(description = "درصد تخفیف محصول (بین ۰ تا ۱۰۰)")
        Double discountPercentage,

        @Schema(description = "تاریخ ایجاد محصول در سیستم")
        LocalDateTime createdAt,

        @Schema(description = "تاریخ آخرین به‌روزرسانی اطلاعات محصول")
        LocalDateTime updatedAt,

        @Schema(description = "کاربری که محصول را ایجاد کرده است")
        String createdBy,

        @Schema(description = "کاربری که آخرین تغییرات را در محصول انجام داده است")
        String lastModifiedBy,

        @Schema(description = "نسخه رکورد محصول (برای کنترل تغییرات همزمان)")
        Long version
) {}
