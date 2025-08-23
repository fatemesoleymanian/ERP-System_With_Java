package com.example.minierp.application.product;

import com.example.minierp.domain.sales.OrderCancelledEvent;
import com.example.minierp.domain.sales.OrderPlacedEvent;
import org.springframework.context.event.EventListener;

public class ProductEventHandler {
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event){
        //for CRM purposes

    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event){
        //for CRM purposes
    }
}
