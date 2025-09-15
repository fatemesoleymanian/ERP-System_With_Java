package com.example.minierp.domain.sales;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.product.id = :productId AND o.status = :status")
    List<Order> findByProductIdAndStatus(Long productId, OrderStatus status);

   List<Order> findByCreatedAtBetweenAndStatus(LocalDateTime fromDateTime, LocalDateTime toDateTime, OrderStatus orderStatus);

//    List<Order> findByStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.product.id = :productId AND o.status = :status AND o.createdAt between :fromDateTime and :toDateTime")
    List<Order> findByProductIdAndStatusAndCreatedAtBetween(Long productId, OrderStatus status, LocalDateTime fromDateTime, LocalDateTime toDateTime);

    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.product.id = :productId AND o.createdAt between :fromDateTime and :toDateTime")
    List<Order> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

    @Query("SELECT o FROM Order o JOIN o.items i WHERE i.product.id = :productId")
    List<Order> findByProductId(Long productId);

    // Paging + Filtering by status
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Filtering by date range
    Page<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Filtering by status + date range
    Page<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);

//    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    // Filtering by multiple criteria
    @Query("SELECT o FROM Order o WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:from IS NULL OR o.createdAt >= :from) " +
            "AND (:to IS NULL OR o.createdAt <= :to) ")
    Page<Order> findFiltered(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
