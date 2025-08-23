package com.example.minierp.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();

    //soft
    void deleteById(Long id);

    void updateById(Long id,Product product);

    List<Product> findByQuantityLessThanEqual(int threshold);

}
