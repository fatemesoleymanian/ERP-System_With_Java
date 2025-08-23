package com.example.minierp.application.accounting;

import com.example.minierp.domain.inventory.InventoryEvent;
import com.example.minierp.domain.inventory.InventoryTransactionType;
import com.example.minierp.domain.sales.OrderCancelledEvent;
import com.example.minierp.domain.sales.OrderItem;
import com.example.minierp.domain.sales.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountingEventHandler {

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event){
        /**TODO **/
    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event){
        /**TODO **/
    }

    @EventListener
    public void handleInventoryUpdate(InventoryEvent event){
        /**TODO **/
    }
}
