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

/**
 * Handles all inventory-related domain events to keep stock synchronized
 * with sales and product lifecycle changes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventHandler {

    private final InventoryService service;
    private final ProcessedEventRepository processedEventRepository;

    /**
     * Check if an event has already been processed to ensure idempotency.
     */
    private boolean isAlreadyProcessed(String eventId) {
        return processedEventRepository.existsById(eventId);
    }

    /**
     * Mark an event as processed in a separate transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markAsProcessed(String eventId) {
        processedEventRepository.save(new ProcessedEvent(eventId, LocalDateTime.now()));
    }

    // ------------------ ORDER EVENTS ------------------

    @EventListener
    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Processing OrderConfirmedEvent: {}", event.id());
        if (isAlreadyProcessed(event.id())) return;

        event.order().getItems().forEach(item ->
                service.recordTransactionAndUpdateProduct(
                        item.getProduct().getId(),
                        InventoryTransactionType.OUT,
                        item.getQuantity(),
                        event.order().getId()
                )
        );

        markAsProcessed(event.id());
    }

    @EventListener
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Processing OrderCancelledEvent: {}", event.id());
        if (isAlreadyProcessed(event.id())) return;

        event.order().getItems().forEach(item ->
                service.recordTransactionAndUpdateProduct(
                        item.getProduct().getId(),
                        InventoryTransactionType.IN,
                        item.getQuantity(),
                        event.order().getId()
                )
        );

        markAsProcessed(event.id());
    }

    @EventListener
    @Transactional
    public void handleOrderUpdated(OrderUpdatedEvent event) {
        log.info("Processing OrderUpdatedEvent: {}", event.id());
        if (isAlreadyProcessed(event.id())) return;

        // Delete old transactions for the order
        service.softDeleteTransactionsByOrderId(event.orderId());

        // Record new updated transactions
        event.newOrder().getItems().forEach(item ->
                service.recordTransactionAndUpdateProduct(
                        item.getProduct().getId(),
                        InventoryTransactionType.OUT,
                        item.getQuantity(),
                        event.orderId()
                )
        );

        markAsProcessed(event.id());
    }

    // ------------------ PRODUCT EVENTS ------------------

    @EventListener
    @Transactional
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Processing ProductCreatedEvent for product {}", event.product().getId());

        service.recordTransaction(
                event.product().getId(),
                InventoryTransactionType.IN,
                event.product().getQuantity(),
                null
        );
    }

    @EventListener
    @Transactional
    public void handleProductUpdated(ProductUpdatedEvent event) {
        log.info("Processing ProductUpdatedEvent for product {}", event.id());

        Product updated = event.product();
        int currentStock = service.getCurrentQuantity(event.id());
        int diff = updated.getQuantity() - currentStock;

        if (diff != 0) {
            InventoryTransactionType type =
                    diff > 0 ? InventoryTransactionType.IN : InventoryTransactionType.OUT;

            service.recordTransaction(
                    event.id(),
                    type,
                    Math.abs(diff),
                    null
            );
        }
    }

    @EventListener
    @Transactional
    public void handleProductDeleted(ProductDeletedEvent event) {
        log.info("Processing ProductDeletedEvent for product {}", event.productId());
        service.softDeleteTransactionsByProduct(event.productId());
    }
}
