package com.example.minierp.domain.sales;

public record OrderCancelledEvent(String id, SaleOrder order, String reason) { }

