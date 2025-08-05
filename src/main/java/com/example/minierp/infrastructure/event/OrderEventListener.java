package com.example.minierp.infrastructure.event;

import com.example.minierp.domain.sales.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @EventListener
    public void handle(OrderPlacedEvent event) {
        log.info("New Order created: {}", event.order().getOrderNumber());
    }
}

