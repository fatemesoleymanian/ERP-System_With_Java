package com.example.minierp.domain.sales;

import java.util.List;

public record OrderUpdatedEvent(Long orderId,Order newOrder, List<OrderItem> oldOrderItems) {
}
