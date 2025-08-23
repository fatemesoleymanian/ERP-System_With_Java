package com.example.minierp.domain.sales;

import java.util.List;

public record OrderUpdatedEvent(Order newOrder, List<OrderItem> oldOrderItems) {
}
