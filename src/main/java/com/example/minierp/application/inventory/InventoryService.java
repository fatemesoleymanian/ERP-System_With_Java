package com.example.minierp.application.inventory;

import com.example.minierp.domain.inventory.*;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.inventory.InventoryTransactionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void recordTransactionAndUpdateProduct(Long productId, InventoryTransactionType type, int quantity, Long orderId) {
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
                .orderId(orderId)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        eventPublisher.publish(new InventoryEvent(product, quantity, type));
    }


    //event driven methods
    @Transactional
    public void recordTransaction(Long productId, InventoryTransactionType type, int quantity,Long orderId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (type == InventoryTransactionType.OUT && product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient inventory");
        }

        InventoryTransaction tx = InventoryTransaction.builder()
                .product(product)
                .type(type)
                .quantity(quantity)
                .orderId(orderId)
                .timestamp(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);

        eventPublisher.publish(new InventoryEvent(product, quantity, type));
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionDto> getProductLedger(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<InventoryTransaction> transactions =
                transactionRepository.findByProductIdOrderByTimestampAsc(productId);

        int runningBalance = 0;
        List<InventoryTransactionDto> ledger = new ArrayList<>();

        for (InventoryTransaction tx : transactions) {
            if (tx.getType() == InventoryTransactionType.IN) {
                runningBalance += tx.getQuantity();
            } else {
                runningBalance -= tx.getQuantity();
            }

            Long orderId = tx.getOrderId() != null ? tx.getOrderId() : 0L;

            ledger.add(new InventoryTransactionDto(
                    tx.getId(),
                    tx.getTimestamp(),
                    tx.getType(),
                    tx.getQuantity(),
                    runningBalance,
                    orderId,
                    productId
            ));
        }

        if (runningBalance != product.getQuantity()) {
            throw new IllegalStateException("Ledger and product quantity mismatch!");
        }
        if (product.getQuantity() < 5) {
            eventPublisher.publish(new LowStockEvent(product));
        }

        return ledger;
    }
    @Transactional
    public void softDeleteTransactionsByProduct(Long productId) {
        transactionRepository.softDeleteByProductId(productId);

    }

    @Transactional
    public void softDeleteTransactionsById(Long id) {
        transactionRepository.softDeleteById(id);
    }

    @Transactional
    public void softDeleteTransactionsByOrderId(Long orderId) {
        transactionRepository.softDeleteByOrderId(orderId);
    }


    @Transactional(readOnly = true)
    public int getCurrentQuantity(Long productId) {
        Integer qty = transactionRepository.findLastQuantityByProductId(productId);
        return qty != null ? qty : 0;
    }

}
