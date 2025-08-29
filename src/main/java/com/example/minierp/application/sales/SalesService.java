package com.example.minierp.application.sales;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.*;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.sales.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
    private final OrderItemRepository orderItemRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
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

    @Transactional
    public void cancelOrder(Long orderId){
        Order order = repository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED){
            throw new RuntimeException("Order is already cancelled");
        }
        for (OrderItem item: order.getItems()){
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() + item.getQuantity());

            productRepository.save(product);
        }
        order.setStatus(OrderStatus.CANCELLED);
        repository.save(order);

        eventPublisher.publish(new OrderCancelledEvent(order));
    }

    @Transactional
    public Order updateOrder(Long orderId, List<CreateOrderRequest.Item> newItems) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cancelled order cannot be updated");
        }

        List<OrderItem> oldItems = new ArrayList<>(order.getItems());

        for (OrderItem oldItem : order.getItems()) {
            Product product = oldItem.getProduct();
            product.setQuantity(product.getQuantity() + oldItem.getQuantity());
            productRepository.save(product);
        }

        orderItemRepository.deleteAll(order.getItems());
        order.getItems().clear();

        List<OrderItem> updatedItems = new ArrayList<>();

        for (CreateOrderRequest.Item newItem : newItems) {
            Product product = productRepository.findById(newItem.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < newItem.quantity()) {
                throw new RuntimeException("موجودی کافی نیست: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - newItem.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(newItem.quantity())
                    .price(product.getPrice())
                    .build();

            updatedItems.add(orderItem);
        }

        order.setItems(updatedItems);
        Order saved = repository.save(order);

        eventPublisher.publish(new OrderUpdatedEvent(orderId, saved, oldItems));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findOrder(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }


    //event driven methods
    @Transactional
    public void cancelOrdersContainingProduct(Long productId) {
        List<Order> affectedOrders = repository.findByProductIdAndStatus(productId, OrderStatus.PLACED);

        for (Order order : affectedOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            repository.save(order);
            eventPublisher.publish(new OrderCancelledEvent(order));
        }
    }

    @Transactional
    public void updatePendingOrdersWithNewProductInfo(Product product) {
        List<Order> pendingOrders = repository.findByProductIdAndStatus(product.getId(), OrderStatus.PLACED);

        for (Order order : pendingOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getId() == product.getId()) {
//                    item.setPrice(product.getPrice());
                    item.setProduct(product);
                }
            }
            repository.save(order);
        }
    }
}
