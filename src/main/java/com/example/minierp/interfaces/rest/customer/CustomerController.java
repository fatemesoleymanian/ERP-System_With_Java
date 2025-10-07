package com.example.minierp.interfaces.rest.customer;

import com.example.minierp.api.common.ApiResponse;
import com.example.minierp.application.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(service.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> findAll(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerResponse> customerPage = service.findAll(active, pageable);
        return ResponseEntity.ok(ApiResponse.success(customerPage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
