package com.example.minierp.interfaces.rest.reports;

import com.example.minierp.domain.sales.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderReportResponse(
        String orderNumber,
        LocalDateTime createdAt,
        String status,
        List<Item> items
) {
    public record Item(String productName, Integer quantity, BigDecimal price) {}

    public static OrderReportResponse from(Order order) {
        return new OrderReportResponse(
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(i -> new Item(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                        .toList()
        );
    }
}

