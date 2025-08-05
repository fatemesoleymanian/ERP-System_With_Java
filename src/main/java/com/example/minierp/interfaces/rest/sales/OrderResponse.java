package com.example.minierp.interfaces.rest.sales;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String orderNumber, LocalDateTime createdAt, List<Item> items) {
    public record Item(String productName, Integer quantity, Double price) {}
}