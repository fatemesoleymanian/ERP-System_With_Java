package com.example.minierp.domain.sales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleOrderRepository extends JpaRepository<SaleOrder, Long> {

 @Query("""
        SELECT o FROM SaleOrder o
        JOIN o.items i
        WHERE i.product.id = :productId AND o.status = :status
    """)
 List<SaleOrder> findByProductIdAndStatus(Long productId, OrderStatus status);

 List<SaleOrder> findByCreatedAtBetweenAndStatus(LocalDateTime from, LocalDateTime to, OrderStatus status);

 @Query("""
        SELECT o FROM SaleOrder o
        JOIN o.items i
        WHERE i.product.id = :productId
        AND o.status = :status
        AND o.createdAt BETWEEN :from AND :to
    """)
 List<SaleOrder> findByProductIdAndStatusAndCreatedAtBetween(Long productId, OrderStatus status, LocalDateTime from, LocalDateTime to);

 @Query("""
        SELECT o FROM SaleOrder o
        JOIN o.items i
        WHERE i.product.id = :productId
        AND o.createdAt BETWEEN :from AND :to
    """)
 List<SaleOrder> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime from, LocalDateTime to);

 @Query("SELECT o FROM SaleOrder o JOIN o.items i WHERE i.product.id = :productId")
 List<SaleOrder> findByProductId(Long productId);

 Page<SaleOrder> findByStatus(OrderStatus status, Pageable pageable);

 List<SaleOrder> findByStatus(OrderStatus status);


 Page<SaleOrder> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

 Page<SaleOrder> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);

 @Query("""
        SELECT o FROM SaleOrder o
        WHERE (:status IS NULL OR o.status = :status)
        AND (:from IS NULL OR o.createdAt >= :from)
        AND (:to IS NULL OR o.createdAt <= :to)
    """)
 Page<SaleOrder> findFiltered(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);

 List<SaleOrder> findByCustomerIdAndStatus(Long customerId, OrderStatus status);

}
