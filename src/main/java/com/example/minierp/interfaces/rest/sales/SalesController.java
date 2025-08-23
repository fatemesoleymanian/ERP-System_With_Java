package com.example.minierp.interfaces.rest.sales;


import com.example.minierp.application.sales.SalesService;
import com.example.minierp.domain.sales.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin
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

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public void cancelOrder(@PathVariable Long orderId){
        service.cancelOrder(orderId);
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public OrderResponse updateOrder(@PathVariable Long orderId, @RequestBody CreateOrderRequest request){
        Order order = service.updateOrder(orderId,request.items());

        List<OrderResponse.Item> itemDtos = order.getItems().stream()
                .map(i -> new OrderResponse.Item(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();

        return new OrderResponse(order.getOrderNumber(), order.getCreatedAt(), itemDtos);
    }

    @GetMapping("/orders")
    public List<Order> getAll(){
        return service.getOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderResponse findById(@PathVariable long id){
        Order order = service.findOrder(id);

        List<OrderResponse.Item> itemDtos = order.getItems().stream()
                .map(i -> new OrderResponse.Item(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();

        return new OrderResponse(order.getOrderNumber(), order.getCreatedAt(), itemDtos);
    }
}

