package com.example.minierp.domain.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    // In InventoryTransactionRepository (updated and new methods)

    @Query("SELECT t FROM InventoryTransaction t WHERE t.product.id = :productId AND t.deletedAt IS NULL ORDER BY t.createdAt ASC, t.id ASC")
    Page<InventoryTransaction> findByProductIdOrderByCreatedAtAsc(@Param("productId") long productId, Pageable pageable);

    @Query("SELECT SUM(CASE WHEN t.type = 'IN' THEN t.quantity ELSE -t.quantity END) " +
            "FROM InventoryTransaction t WHERE t.product.id = :productId AND t.deletedAt IS NULL " +
            "AND (t.createdAt < :beforeDate OR (t.createdAt = :beforeDate AND t.id < :beforeId))")
    Integer getBalanceBefore(@Param("productId") long productId,
                             @Param("beforeDate") LocalDateTime beforeDate,
                             @Param("beforeId") Long beforeId);

    @Query("SELECT SUM(CASE WHEN t.type = 'IN' THEN t.quantity ELSE -t.quantity END) " +
            "FROM InventoryTransaction t WHERE t.product.id = :productId AND t.deletedAt IS NULL")
    Integer getTotalBalance(@Param("productId") long productId);
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
