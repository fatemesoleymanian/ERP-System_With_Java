package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;

public record InventoryRequest(Long productId, InventoryTransactionType type, Integer quantity) {
}
