package com.example.minierp.domain.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @Query("SELECT t FROM InventoryTransaction t WHERE t.product.id = :productId AND t.deletedAt IS NULL ORDER BY t.createdAt ASC")
    List<InventoryTransaction> findByProductIdOrderByCreatedAtAsc(long productId);

    @Modifying
    @Query("UPDATE InventoryTransaction t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = ?1 AND t.deletedAt IS NULL")
    int softDeleteById(Long id);

    @Modifying
    @Query("UPDATE InventoryTransaction t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.product.id = ?1")
    int softDeleteByProductId(Long productId);

    @Modifying
    @Query("UPDATE InventoryTransaction t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.orderId = ?1")
    int softDeleteByOrderId(Long orderId);

    @Query("SELECT t.quantity FROM InventoryTransaction t WHERE t.product.id = :productId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    Integer findLastQuantityByProductId(Long productId);
}
