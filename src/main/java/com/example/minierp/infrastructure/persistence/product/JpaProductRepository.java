package com.example.minierp.infrastructure.persistence.product;

import com.example.minierp.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByQuantityLessThanEqualAndDeletedAtNull(int threshold);

    @Query("select p from Product p where p.deletedAt is NULL")
    List<Product> findByDeletedAtNull();

    @Query("select p from Product p where p.id = :id AND p.deletedAt is NULL")
    Optional<Product> findByIdAndDeletedAtNull(long id);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(Long categoryId);

    boolean existsBySku(String sku);

    @Query("SELECT SUM(p.quantity) FROM Product p WHERE p.deletedAt IS NULL")
    Optional<Integer> sumQuantity();
}
