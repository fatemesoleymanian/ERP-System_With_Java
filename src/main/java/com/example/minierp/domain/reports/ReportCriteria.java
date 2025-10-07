package com.example.minierp.domain.reports;

import com.example.minierp.domain.sales.OrderStatus;

import java.time.LocalDateTime;

public  record ReportCriteria(
        ReportType type,
        LocalDateTime from,
        LocalDateTime to,
        Long productId,
        Long customerId,
        Long categoryId,
        OrderStatus orderStatus
) {
}
