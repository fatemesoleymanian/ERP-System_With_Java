package com.example.minierp.application.product;

import com.example.minierp.domain.product.*;
import com.example.minierp.domain.shared.DomainEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    private final DomainEventPublisher eventPublisher;

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Product createProduct(Product product){
        Product saved = repository.save(product);
        eventPublisher.publish(new ProductCreatedEvent(saved));
        return saved;
    }

//    @Cacheable("products")
    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(long id){
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + id + " not found"));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteById(long id){
        repository.deleteById(id);
        eventPublisher.publish(new ProductDeletedEvent(id));
    }

    @CacheEvict(value = "products", allEntries = true)
    public void updateById(long id, Product product){
        repository.updateById(id, product);
        eventPublisher.publish(new ProductUpdatedEvent(id, product));
    }
}
