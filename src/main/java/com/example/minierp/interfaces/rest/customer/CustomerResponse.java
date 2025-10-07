package com.example.minierp.interfaces.rest.customer;

import com.example.minierp.domain.customer.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "پاسخ مشتری")
public record CustomerResponse(

        @Schema(description = "شناسه مشتری")
        Long id,

        @Schema(description = "شماره مشتری (کد منحصر به‌فرد مشتری در سیستم)")
        String customerNumber,

        @Schema(description = "نام مشتری")
        String name,

        @Schema(description = "نوع مشتری (حقیقی یا حقوقی)")
        CustomerType type,

        @Schema(description = "نام شخص تماس مشتری")
        String contactPerson,

        @Schema(description = "شماره تلفن مشتری")
        String phone,

        @Schema(description = "آدرس ایمیل مشتری")
        String email,

        @Schema(description = "آدرس صورتحساب مشتری")
        String billingAddress,

        @Schema(description = "آدرس ارسال کالا یا خدمات مشتری")
        String shippingAddress,

        @Schema(description = "سقف اعتبار مشتری (حداکثر مبلغ مجاز خرید نسیه)")
        Double creditLimit,

        @Schema(description = "وضعیت فعال بودن مشتری (فعال یا غیرفعال)")
        Boolean active,

        @Schema(description = "تاریخ ایجاد رکورد مشتری")
        LocalDateTime createdAt,

        @Schema(description = "تاریخ آخرین به‌روزرسانی اطلاعات مشتری")
        LocalDateTime updatedAt,

        @Schema(description = "کاربری که مشتری را ایجاد کرده است")
        String createdBy,

        @Schema(description = "کاربری که آخرین تغییرات را در مشتری انجام داده است")
        String lastModifiedBy
) {}
