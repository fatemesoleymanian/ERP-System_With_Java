package com.example.minierp.interfaces.rest.product;

import com.example.minierp.application.product.ProductService;
import com.example.minierp.domain.product.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService service;

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
    public ProductDto getById(@PathVariable Long id){
        return service.getById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(()-> new RuntimeException("Product not found"));

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteById(id);
    }
}
