package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;

import java.time.LocalDateTime;

public record InventoryTransactionDto(
        long id,
        LocalDateTime timestamp,
        InventoryTransactionType type,
        int quantity,
        int balance,
        Long orderId,
        Long productId
) {
}
