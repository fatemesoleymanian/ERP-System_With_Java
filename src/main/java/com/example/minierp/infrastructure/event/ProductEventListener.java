package com.example.minierp.infrastructure.event;

import com.example.minierp.domain.product.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);

    @EventListener
    public void handleProductCreated(ProductCreatedEvent event){
        log.info("New product created : {}",event.product().getName());
    }
}
