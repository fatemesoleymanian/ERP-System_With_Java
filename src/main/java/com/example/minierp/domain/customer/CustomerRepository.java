package com.example.minierp.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Page<Customer> findAllByActive(Boolean active, Pageable pageable);

    // Fetch all non-deleted (as before, if needed)
    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    Page<Customer> findAllNonDeleted(Pageable pageable);

    // Fetch deleted only (bypasses @Where)
    @Query(value = "SELECT * FROM customer WHERE deleted_at IS NOT NULL", nativeQuery = true)
    Page<Customer> findAllDeleted(Pageable pageable);

    // Fetch a specific deleted customer by ID (bypasses @Where)
    @Query(value = "SELECT * FROM customer WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
    Customer findDeletedById(Long id);

    Integer countByActiveTrue();
}
