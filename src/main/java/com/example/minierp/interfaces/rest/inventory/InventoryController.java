package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.application.inventory.InventoryService;
import io.jsonwebtoken.lang.Collections;
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
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public void record(@RequestBody @Valid InventoryRequest request){
        service.recordTransactionAndUpdateProduct(request.productId(),request.type(),request.quantity(),request.orderId());
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<InventoryTransactionDto>> findLedger(@PathVariable Long productId) {
        try {
            List<InventoryTransactionDto> ledger = service.getProductLedger(productId);
            return ResponseEntity.ok(ledger);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.emptyList());
            }
            throw e;
        }
    }
}
