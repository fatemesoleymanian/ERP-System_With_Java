package com.example.minierp.application.sales;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.*;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.sales.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class SalesService {
    private final ProductRepository productRepository;
    private final OrderRepository repository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(List<CreateOrderRequest.Item> items) {
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .build();

        for (CreateOrderRequest.Item item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < item.quantity()) {
                throw new RuntimeException("موجودی کافی نیست: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - item.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.quantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        Order saved = repository.save(order);
        eventPublisher.publish(new OrderPlacedEvent(saved));

        return saved;
    }
}
