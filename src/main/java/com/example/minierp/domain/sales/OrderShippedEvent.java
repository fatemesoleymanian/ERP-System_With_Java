package com.example.minierp.domain.sales;


public record OrderShippedEvent(String id, SaleOrder order, String trackingCode) { }

