package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.application.inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        service.recordTransactionAndUpdateProduct(request.productId(),request.type(),request.quantity());
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public List<InventoryTransactionDto> findLedger(@PathVariable long productId){
        return service.getProductLedger(productId);
    }
}
