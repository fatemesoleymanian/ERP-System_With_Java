package com.example.minierp.interfaces.rest.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "پاسخ داشبورد")
public record KpiResponse(
        @Schema(description = "تعداد سفارشات با تاریخ createdAt امروز")
        Integer todayOrders,

        @Schema(description = "مجموع ستون quantity از تمام محصولات ")
        Integer totalInventory,

        @Schema(description = "مجموع totalAmount سفارشات با وضعیت COMPLETED یا PAID در ماه جاری.")
        BigDecimal monthlyRevenue,

        @Schema(description = "تعداد مشتریان فعال (active: true)")
        Integer totalCustomers,

        @Schema(description = "۵ سفارش آخر (بر اساس createdAt descending) مشتری")
        List<LatestOrderDto> latestOrders
) {
}
