package com.example.minierp.application.sales;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductDeletedEvent;
import com.example.minierp.domain.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesEventHandler {
    private final SalesService service;

    @EventListener
    public void handleProductUpdated(ProductUpdatedEvent event){
        Product updatedProduct = event.product();

        service.updatePendingOrdersWithNewProductInfo(updatedProduct);
    }
    @EventListener
    public void handleProductDeleted(ProductDeletedEvent event){
        Long productId = event.productId();

        service.cancelOrdersContainingProduct(productId);
    }

}
