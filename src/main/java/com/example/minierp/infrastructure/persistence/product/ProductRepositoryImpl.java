package com.example.minierp.infrastructure.persistence.product;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaRepo;

    @Override
    public Product save(Product product) {
        return jpaRepo.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepo.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepo.findByDeletedAtFalse();
    }
    @Override
    public void deleteById(Long id) {
        Optional<Product> existingProductOpt = jpaRepo.findById(id);
        if (existingProductOpt.isPresent()) {
            Product product = existingProductOpt.get();
            product.setDeletedAt(LocalDateTime.now());
            jpaRepo.save(product);
        } else {
            throw new EntityNotFoundException("Product with ID " + id + " not found");
        }
    }

    @Override
    public void updateById(Long id, Product product) {
        Optional<Product> existingProductOpt = jpaRepo.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();

            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setSku(product.getSku());

            jpaRepo.save(existingProduct);
        } else {
            throw new EntityNotFoundException("Product with ID " + id + " not found");
        }
    }

    @Override
    public List<Product> findByQuantityLessThanEqual(int threshold) {
        return jpaRepo.findByQuantityLessThanEqualAndDeletedAtFalse(threshold);
    }
}
