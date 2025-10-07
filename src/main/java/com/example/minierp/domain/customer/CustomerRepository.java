package com.example.minierp.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Page<Customer> findAllByActive(Boolean active, Pageable pageable);
}
