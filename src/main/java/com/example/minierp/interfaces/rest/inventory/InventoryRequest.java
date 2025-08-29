package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import jakarta.annotation.Nullable;

public record InventoryRequest(Long productId, InventoryTransactionType type, Integer quantity,@Nullable Long orderId) {
}
