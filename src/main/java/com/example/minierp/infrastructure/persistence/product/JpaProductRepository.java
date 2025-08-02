package com.example.minierp.infrastructure.persistence.product;

import com.example.minierp.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, Long> {
}
