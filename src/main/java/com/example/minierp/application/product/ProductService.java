package com.example.minierp.application.product;

import com.example.minierp.domain.common.exceptions.DynamicTextException;
import com.example.minierp.domain.product.*;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final VatRateRepository vatRateRepository;
    private final CategoryRepository categoryRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public ProductResponse create(CreateProductRequest request) {

        if (repository.existsBySku(request.sku())){
            throw new DynamicTextException("محصول تکراری است.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException(request.categoryId(), "دسته بندی "));

        // --- FETCH VAT RATE ---
        VatRate vatRate = vatRateRepository.findById(request.vatRateId())
                .orElseThrow(() -> new NotFoundException(request.vatRateId(), "نرخ مالیات "));
        // ----------------------

        Product product = Product.builder()
                .name(request.name())
                .sku(request.sku())
                .price(request.price())
                .quantity(request.quantity())
                .discountValue(request.discountValue())
                .discountPercentage(request.discountPercentage())
                .category(category) // Set category here
                .vatRate(vatRate) // Set VAT rate here
                .build();


        Product saved = repository.save(product);
        eventPublisher.publish(new ProductCreatedEvent(saved));
        return mapToResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "محصول "));

        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setDiscountValue(request.discountValue());
        product.setDiscountPercentage(request.discountPercentage());

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException(request.categoryId(), "دسته بندی "));
        product.setCategory(category);

        // --- FETCH AND SET VAT RATE ---
        VatRate vatRate = vatRateRepository.findById(request.vatRateId())
                .orElseThrow(() -> new NotFoundException(request.vatRateId(), "نرخ مالیات "));
        product.setVatRate(vatRate);


        Product saved = repository.save(product);
        eventPublisher.publish(new ProductUpdatedEvent(id,saved));
        return mapToResponse(saved);
    }
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "محصول "));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        Page<Product> page = repository.findAll().stream() // optional: could implement paging in repo
                .filter(p -> p.getDeletedAt() == null)
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())));
        return page.map(this::mapToResponse);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id); // soft delete inside repository
        eventPublisher.publish(new ProductDeletedEvent(id));
    }

    private ProductResponse mapToResponse(Product product) {
        // Handle potential null VAT rate if not set, though it should be required in DTO
        Long vatRateId = product.getVatRate() != null ? product.getVatRate().getId() : null;
        String vatRateName = product.getVatRate() != null ? product.getVatRate().getName() : null;
        BigDecimal vatRate = product.getVatRate() != null ? product.getVatRate().getRate() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getCategory().getName(),
                // --- ADDED VAT RATE FIELDS ---
                vatRateId,
                vatRateName,
                vatRate,
                // -----------------------------
                product.getPrice(),
                product.getQuantity(),
                product.getDiscountValue(),
                product.getDiscountPercentage(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getCreatedBy(),
                product.getLastModifiedBy(),
                product.getVersion()
        );
    }
    /** TODO **/ //SEARCH & FILTER
}
