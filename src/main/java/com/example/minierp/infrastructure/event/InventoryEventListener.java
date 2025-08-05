package com.example.minierp.infrastructure.event;

import com.example.minierp.domain.inventory.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {
    private static final Logger log = LoggerFactory.getLogger(InventoryEventListener.class);

    @EventListener
    public void handleInventoryEvent(InventoryEvent event){
        log.info("Warehouse Transaction: {} | Item: {} | Quantity: {}",
                event.type(), event.product().getName(), event.quantity());
    }
}
