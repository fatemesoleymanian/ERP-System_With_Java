package com.example.minierp.application.inventory;

import com.example.minierp.domain.common.exceptions.InsufficientStockException;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.inventory.InventoryEvent;
import com.example.minierp.domain.inventory.InventoryTransaction;
import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.inventory.LowStockEvent;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.inventory.InventoryTransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final com.example.minierp.domain.inventory.InventoryTransactionRepository transactionRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * Record a transaction and update the product quantity
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void recordTransactionAndUpdateProduct(Long productId, InventoryTransactionType type, int quantity, Long orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(productId,"Product "));

        if (type == InventoryTransactionType.OUT && product.getQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(),quantity);
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
                .orderId(orderId)
                .build();

        transactionRepository.save(tx);

        // publish event for other modules
        eventPublisher.publish(new com.example.minierp.domain.inventory.InventoryEvent(product, quantity, type));

        // publish low stock event if needed
        if (product.getQuantity() < 5) {
            eventPublisher.publish(new com.example.minierp.domain.inventory.LowStockEvent(product));
        }
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void recordTransaction(Long productId, InventoryTransactionType type, int quantity, Long orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(productId,"Product "));

        if (type == InventoryTransactionType.OUT && product.getQuantity() < quantity) {
            throw new InsufficientStockException(product.getName(),quantity);
        }

        int updatedQty = type == InventoryTransactionType.IN
                ? product.getQuantity() + quantity
                : product.getQuantity() - quantity;


        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .orderId(orderId)
                .build();

        transactionRepository.save(tx);

        // publish event for other modules
        eventPublisher.publish(new InventoryEvent(product, quantity, type));

        // publish low stock event if needed
        if (product.getQuantity() < 5) {
            eventPublisher.publish(new LowStockEvent(product));
        }
    }

    /**
     * Retrieve product ledger with running balance
     */

    @Transactional(readOnly = true)
    public Page<InventoryTransactionDto> getProductLedger(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(productId, "Product"));

        Integer totalBalance = transactionRepository.getTotalBalance(productId);
        int expectedBalance = totalBalance != null ? totalBalance : 0;
        if (expectedBalance != product.getQuantity()) {
            throw new IllegalStateException("Ledger and product quantity mismatch!");
        }

        Page<InventoryTransaction> txPage = transactionRepository.findByProductIdOrderByCreatedAtAsc(productId, pageable);

        int runningBalance = 0;
        if (!txPage.isEmpty()) {
            InventoryTransaction first = txPage.getContent().get(0);
            Integer initial = transactionRepository.getBalanceBefore(productId, first.getCreatedAt(), first.getId());
            runningBalance = initial != null ? initial : 0;
        }

        List<InventoryTransactionDto> ledger = new ArrayList<>();

        for (InventoryTransaction tx : txPage.getContent()) {
            runningBalance = tx.getType() == InventoryTransactionType.IN
                    ? runningBalance + tx.getQuantity()
                    : runningBalance - tx.getQuantity();

            Long orderId = tx.getOrderId() != null ? tx.getOrderId() : 0L;

            ledger.add(new InventoryTransactionDto(
                    tx.getId(),
                    tx.getCreatedAt(),
                    tx.getType(),
                    tx.getQuantity(),
                    runningBalance,
                    orderId,
                    productId
            ));
        }

        return new PageImpl<>(ledger, pageable, txPage.getTotalElements());
    }
    /**
     * Soft delete transactions by product ID
     */
    @Transactional
    public void softDeleteTransactionsByProduct(Long productId) {
        transactionRepository.softDeleteByProductId(productId);
    }

    /**
     * Soft delete a single transaction by ID
     */
    @Transactional
    public void softDeleteTransactionsById(Long id) {
        transactionRepository.softDeleteById(id);
    }

    /**
     * Soft delete transactions by order ID
     */
    @Transactional
    public void softDeleteTransactionsByOrderId(Long orderId) {
        transactionRepository.softDeleteByOrderId(orderId);
    }

    /**
     * Get current quantity of a product
     */
    @Transactional(readOnly = true)
    public int getCurrentQuantity(Long productId) {
        Integer qty = transactionRepository.findLastQuantityByProductId(productId);
        return qty != null ? qty : 0;
    }
}
