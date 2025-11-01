package com.example.minierp.interfaces.rest.product;

import com.example.minierp.api.common.ApiResponse;
import com.example.minierp.domain.customer.Customer;
import com.example.minierp.domain.product.VatRate;
import com.example.minierp.domain.product.VatRateRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/vat-rates")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class VatRateController {

    private final VatRateRepository vatRateRepository;

    @GetMapping("/list")
    public ResponseEntity<List<VatRateResponse>> getAllVatRates() {
        List<VatRateResponse> responses = vatRateRepository.findAll().stream()
                .map(VatRateResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VatRateResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VatRate> vatRatePage = vatRateRepository.findAll(pageable);
        Page<VatRateResponse> responsePage = vatRatePage.map(VatRateResponse::fromEntity);

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @PostMapping
    public ResponseEntity<VatRateResponse> createVatRate(@Valid @RequestBody VatRateRequest request) {
        VatRate newVatRate = VatRate.builder()
                .name(request.name())
                .rate(request.rate())
                .isDefault(request.isDefault())
                .build();
        VatRate saved = vatRateRepository.save(newVatRate);
        return ResponseEntity.ok(VatRateResponse.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VatRateResponse> updateVatRate(@PathVariable Long id, @Valid @RequestBody VatRateRequest request) {
        return vatRateRepository.findById(id)
                .map(vatRate -> {
                    vatRate.setName(request.name());
                    vatRate.setRate(request.rate());
                    vatRate.setDefault(request.isDefault());
                    VatRate updated = vatRateRepository.save(vatRate);
                    return ResponseEntity.ok(VatRateResponse.fromEntity(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVatRate(@PathVariable Long id) {
        if (!vatRateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vatRateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}