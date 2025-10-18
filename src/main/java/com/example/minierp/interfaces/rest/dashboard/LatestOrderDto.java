package com.example.minierp.interfaces.rest.dashboard;

import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.domain.sales.SaleOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ساختار جدید برای نمایش سفارش در داشبورد
public record LatestOrderDto(
        Long id,
        String orderNumber,
        String customerName, // Assuming you have this on SaleOrder or can fetch it
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    // Factory method to convert domain model SaleOrder to DTO
    public static LatestOrderDto from(SaleOrder order) {
        // You'll need to map order.getCustomer().getName() to customerName
        String customerName = order.getCustomer() != null ? order.getCustomer().getName() : "N/A";

        return new LatestOrderDto(
                order.getId(),
                order.getOrderNumber(),
                customerName,
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}