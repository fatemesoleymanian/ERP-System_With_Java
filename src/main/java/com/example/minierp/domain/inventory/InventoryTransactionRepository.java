package com.example.minierp.domain.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProductIdOrderByTimestampAsc(long ProductId);

    @Modifying
    @Query("UPDATE InventoryTransaction t SET t.deletedAt = CURRENT_TIMESTAMP WHERE t.id = ?1 AND t.deletedAt IS NULL")
    int softDeleteById(Long id);

    @Modifying
    @Query("UPDATE InventoryTransaction t SET t.deletedAt = CURRENT_TIMESTAMP " +
            "WHERE t.product.id = ?1 AND t.deletedAt IS NULL")
    int softDeleteByProductId(Long productId);

}

