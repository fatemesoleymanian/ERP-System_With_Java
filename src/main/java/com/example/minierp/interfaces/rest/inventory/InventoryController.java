package com.example.minierp.interfaces.rest.inventory;

import com.example.minierp.application.inventory.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public void record(@RequestBody @Valid InventoryRequest request){
        service.recordTransaction(request.productId(),request.type(),request.quantity());
    }
}
