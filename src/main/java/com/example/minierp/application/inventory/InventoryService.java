package com.example.minierp.application.inventory;

import com.example.minierp.domain.inventory.InventoryEvent;
import com.example.minierp.domain.inventory.InventoryTransaction;
import com.example.minierp.domain.inventory.InventoryTransactionRepository;
import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.shared.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public void recordTransaction(Long productId, InventoryTransactionType type, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (type == InventoryTransactionType.OUT && product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory");
        }

        int updatedQty = type == InventoryTransactionType.IN
                ? product.getQuantity() + quantity
                : product.getQuantity() - quantity;

        product.setQuantity(updatedQty);
        productRepository.save(product);

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        eventPublisher.publish(new InventoryEvent(product, quantity, type));
    }

}
