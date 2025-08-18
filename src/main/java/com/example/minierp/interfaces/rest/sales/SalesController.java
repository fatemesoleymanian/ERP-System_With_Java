package com.example.minierp.interfaces.rest.sales;


import com.example.minierp.application.sales.SalesService;
import com.example.minierp.domain.sales.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService service;


    @PostMapping("/order")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public OrderResponse placeOrder(@RequestBody CreateOrderRequest request) {
        Order order = service.placeOrder(request.items());

        List<OrderResponse.Item> itemDtos = order.getItems().stream()
                .map(i -> new OrderResponse.Item(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();

        return new OrderResponse(order.getOrderNumber(), order.getCreatedAt(), itemDtos);
    }
}

