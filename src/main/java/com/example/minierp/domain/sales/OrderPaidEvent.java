package com.example.minierp.domain.sales;

import java.math.BigDecimal;

public record OrderPaidEvent(String id, Order order, BigDecimal amount) {
}
