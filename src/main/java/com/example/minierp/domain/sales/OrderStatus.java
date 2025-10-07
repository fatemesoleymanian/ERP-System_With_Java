package com.example.minierp.domain.sales;

public enum OrderStatus {
    DRAFT,      // Order created but not yet confirmed
    PLACED,     // Customer placed the order
    CONFIRMED,  // Admin confirmed and stock reserved
    PAID,       // Payment completed
    SHIPPED,    // Order sent to customer
    COMPLETED,  // Order received and finalized
    CANCELLED   // Order cancelled by customer/admin
}
