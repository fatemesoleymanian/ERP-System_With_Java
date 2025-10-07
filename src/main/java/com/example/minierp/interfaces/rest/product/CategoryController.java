package com.example.minierp.interfaces.rest.product;

import com.example.minierp.application.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY_MANAGER')")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Validated @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','INVENTORY_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id,
                                                   @Validated @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

