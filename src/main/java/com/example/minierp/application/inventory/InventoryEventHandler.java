package com.example.minierp.application.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductCreatedEvent;
import com.example.minierp.domain.product.ProductDeletedEvent;
import com.example.minierp.domain.product.ProductUpdatedEvent;
import com.example.minierp.domain.sales.OrderCancelledEvent;
import com.example.minierp.domain.sales.OrderItem;
import com.example.minierp.domain.sales.OrderPlacedEvent;
import com.example.minierp.domain.sales.OrderUpdatedEvent;
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
    public void handleOrderCancelled(OrderCancelledEvent event){
        for (OrderItem item : event.order().getItems()) {
            service.recordTransaction(
                    item.getProduct().getId(),
                    InventoryTransactionType.IN,
                    item.getQuantity()
            );
        }
    }
    @EventListener
    public void handleOrderUpdated(OrderUpdatedEvent event){
        //delete the old items
        for (OrderItem item : event.oldOrderItems()){
            service.softDeleteTransactionsById(item.getId());
        }

        //handleOrderPlaced
        for (OrderItem item : event.newOrder().getItems()) {
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

    @EventListener
    public void handleProductUpdated(ProductUpdatedEvent event){
        Product updated = event.product();

        int currentStock = service.getCurrentQuantity(updated.getId());
        int diff = updated.getQuantity() - currentStock;

        if (diff != 0) {
            InventoryTransactionType type =
                    diff > 0 ? InventoryTransactionType.IN : InventoryTransactionType.OUT;

            service.recordTransaction(updated.getId(), type, Math.abs(diff));
        }
    }

    @EventListener
    public void handleProductDeleted(ProductDeletedEvent event){
        Long productId = event.productId();
        service.softDeleteTransactionsByProduct(productId);
    }

}
