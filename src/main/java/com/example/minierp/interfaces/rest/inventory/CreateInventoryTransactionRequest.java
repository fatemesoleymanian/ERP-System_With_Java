package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryTransactionRequest(
        @NotNull(message = "محصول الزامیست.")
        @Min(value = 1, message = "شناسه محصول باید عددی مثبت باشد.")
        Long productId,

        @NotNull(message = "نوع تراکنش الزامیست.")
        InventoryTransactionType type,

        @NotNull(message = "موجودی الزامیست.")
        @Min(value = 1, message = "موجودی حداقل 1 باید باشد.")
        int quantity,

        Long orderId // optional
) {}
