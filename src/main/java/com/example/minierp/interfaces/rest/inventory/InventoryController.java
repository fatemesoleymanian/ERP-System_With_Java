package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.application.inventory.InventoryService;
import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryController {

    private final InventoryService service;

    /**
     * Record an inventory transaction manually
     */
    @PostMapping("/transaction")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> recordTransaction(
            @RequestBody @Valid CreateInventoryTransactionRequest request) {

        service.recordTransactionAndUpdateProduct(
                request.productId(),
                request.type(),
                request.quantity(),
                request.orderId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    /**
     * Get inventory ledger for a product
     */
    @GetMapping("/ledger/{productId}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<InventoryTransactionDto>>> getProductLedger(
            @PathVariable Long productId) {

        List<InventoryTransactionDto> ledger = service.getProductLedger(productId);
        return ResponseEntity.ok(ApiResponse.success(ledger));
    }
}
