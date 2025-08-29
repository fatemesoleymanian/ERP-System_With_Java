package com.example.minierp.interfaces.rest.product;

import com.example.minierp.application.product.ProductService;
import com.example.minierp.domain.product.Product;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductDto create(@RequestBody @Valid CreateProductRequest request){
        Product product = ProductMapper.toEntity(request);
        Product saved = service.createProduct(product);
        return ProductMapper.toDto(saved);
    }
    @GetMapping
    public List<ProductDto> getAll(){
        return service.getAll()
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        try {
            Product product = service.getById(id);
            return ResponseEntity.ok(ProductMapper.toDto(product));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public void update(@PathVariable Long id,@RequestBody @Valid CreateProductRequest request){
        Product product = ProductMapper.toEntity(request);
        service.updateById(id, product);
    }
}
