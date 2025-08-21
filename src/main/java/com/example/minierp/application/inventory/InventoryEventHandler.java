package com.example.minierp.application.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductCreatedEvent;
import com.example.minierp.domain.sales.OrderItem;
import com.example.minierp.domain.sales.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final InventoryService service;

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event){
        for (OrderItem item : event.order().getItems()) {
            service.recordTransaction(
                    item.getProduct().getId(),
                    InventoryTransactionType.OUT,
                    item.getQuantity()
            );
        }
    }
    @EventListener
    public void handleProductCreate(ProductCreatedEvent event){
            service.recordTransaction(
                   event.product().getId(),
                    InventoryTransactionType.IN,
                    event.product().getQuantity()
            );
    }

}
