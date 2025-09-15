package com.example.minierp.domain.sales;


public record OrderShippedEvent(String id, Order order, String trackingCode) {
}
