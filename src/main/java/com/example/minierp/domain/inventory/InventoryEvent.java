package com.example.minierp.domain.inventory;

import com.example.minierp.domain.product.Product;

public record InventoryEvent (Product product,int quantity, InventoryTransactionType type){ }
