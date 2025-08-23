package com.example.minierp.infrastructure.persistence.product;

import com.example.minierp.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByQuantityLessThanEqualAndDeletedAtFalse(int threshold);

    List<Product> findByDeletedAtFalse();
}
