package com.example.minierp.domain.sales;

import java.util.List;

public record OrderUpdatedEvent(String id, Long orderId, SaleOrder newOrder, List<OrderItem> oldOrderItems) { }

