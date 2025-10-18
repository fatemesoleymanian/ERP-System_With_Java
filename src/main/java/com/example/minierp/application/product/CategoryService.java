package com.example.minierp.application.product;


import com.example.minierp.domain.common.exceptions.DynamicTextException;
import com.example.minierp.domain.product.Category;
import com.example.minierp.domain.product.CategoryRepository;
import com.example.minierp.domain.product.Product;
import com.example.minierp.interfaces.rest.product.CategoryResponse;
import com.example.minierp.interfaces.rest.product.CreateCategoryRequest;
import com.example.minierp.interfaces.rest.product.ProductResponse;
import com.example.minierp.interfaces.rest.product.UpdateCategoryRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CreateCategoryRequest request) {

        if (categoryRepository.existsByName(request.name())){
            throw new DynamicTextException("دسته بندی تکراری است.");
        }
        Category category = Category.builder()
                .name(request.name())
                .build();
        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Pageable pageable) {
        Page<Category> page = categoryRepository.findAll()
                .stream()
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())));
        return page.map(this::mapToResponse);
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
        return new CategoryResponse(category.getId(), category.getName());
    }

    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));

        category.setName(request.name());
        Category updated = categoryRepository.save(category);
        return new CategoryResponse(updated.getId(), updated.getName());
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }
}

