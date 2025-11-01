package com.example.minierp.domain.product;

import com.example.minierp.domain.customer.Customer;
import com.example.minierp.domain.product.VatRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VatRateRepository extends JpaRepository<VatRate, Long> {
    Optional<VatRate> findByIsDefaultTrue();

    Page<VatRate> findAll(Pageable pageable);
}
