package com.example.minierp.infrastructure.persistence.product;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        return jpaRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public List<Product> findByQuantityLessThanEqual(int threshold) {
        return jpaRepo.findByQuantityLessThanEqual(threshold);
    }
}
