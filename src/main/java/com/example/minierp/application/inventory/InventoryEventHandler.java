package com.example.minierp.application.inventory;

import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductCreatedEvent;
import com.example.minierp.domain.product.ProductDeletedEvent;
import com.example.minierp.domain.product.ProductUpdatedEvent;
import com.example.minierp.domain.sales.*;
import com.example.minierp.domain.shared.ProcessedEvent;
import com.example.minierp.domain.shared.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventHandler {

    private final InventoryService service;
    private final ProcessedEventRepository processedEventRepository;


    private boolean isAlreadyProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    } @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markAsProcessed(String eventId) {
        processedEventRepository.save(new ProcessedEvent(eventId, LocalDateTime.now()));
    }
    @EventListener
    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event){
        log.info(event.id());
        if (isAlreadyProcessed(event.id())) return;
        for (OrderItem item : event.order().getItems()) {
            service.recordTransaction(
                    item.getProduct().getId(),
                    InventoryTransactionType.OUT,
                    item.getQuantity(),
                    event.order().getId() );
        }
        markAsProcessed(event.id());
    }

    @EventListener
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event){
        if (isAlreadyProcessed(event.id())) return;

        for (OrderItem item : event.order().getItems()) {
            service.recordTransaction(
                    item.getProduct().getId(),
                    InventoryTransactionType.IN,
                    item.getQuantity(),
                    event.order().getId()
            );
        }

        markAsProcessed(event.id());
    }
    @EventListener
    @Transactional
    public void handleOrderUpdated(OrderUpdatedEvent event){
        if (isAlreadyProcessed(event.id())) return;

        //delete the old items
            service.softDeleteTransactionsByOrderId(event.orderId());

        //handleOrderPlaced
        for (OrderItem item : event.newOrder().getItems()) {
            service.recordTransaction(
                    item.getProduct().getId(),
                    InventoryTransactionType.OUT,
                    item.getQuantity(),
                    event.orderId()
            );
        }

        markAsProcessed(event.id());
    }
    @EventListener
    public void handleProductCreate(ProductCreatedEvent event){
            service.recordTransaction(
                   event.product().getId(),
                    InventoryTransactionType.IN,
                    event.product().getQuantity(),
                    null
            );
    }

    @EventListener
    public void handleProductUpdated(ProductUpdatedEvent event){

        Product updated = event.product();

        int currentStock = service.getCurrentQuantity(event.id());

        int diff = updated.getQuantity() - currentStock;

        if (diff != 0) {
            InventoryTransactionType type =
                    diff > 0 ? InventoryTransactionType.IN : InventoryTransactionType.OUT;

            service.recordTransaction(event.id(), type, Math.abs(diff),null);
        }
    }

    @EventListener
    public void handleProductDeleted(ProductDeletedEvent event){
        Long productId = event.productId();
        service.softDeleteTransactionsByProduct(productId);
    }

}
