package com.example.minierp.infrastructure.event;

import com.example.minierp.domain.inventory.LowStockEvent;
import com.example.minierp.domain.sales.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StockEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @EventListener
    public void handle(LowStockEvent event) {
        log.info("Low Stock for : {}", event.product(), " with quantity:", event.product().getQuantity());
    }
}
