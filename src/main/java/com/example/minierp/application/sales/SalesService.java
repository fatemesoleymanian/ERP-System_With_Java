package com.example.minierp.application.sales;

import com.example.minierp.api.common.AuthMethods;
import com.example.minierp.domain.common.exceptions.DynamicTextException;
import com.example.minierp.domain.common.exceptions.InsufficientStockException;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.common.exceptions.OrderAlreadyCancelledException;
import com.example.minierp.domain.customer.CustomerRepository;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.*;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.sales.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class SalesService {
    private final ProductRepository productRepository;
    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final DomainEventPublisher eventPublisher;

    private final CustomerRepository customerRepository;

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Order placeOrder(List<CreateOrderRequest.CreateOrderItem> items,
                            Long customerId, BigDecimal orderDiscountValue,
                            BigDecimal orderDiscountPercent, LocalDateTime desiredDeliveryFrom,
                            LocalDateTime desiredDeliveryTo) {
        log.info("Placing new order with {} items", items.size());

        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .status(OrderStatus.PLACED)
                .orderDiscountValue(orderDiscountValue)
                .orderDiscountPercent(orderDiscountPercent)
                .desiredDeliveryFrom(desiredDeliveryFrom)
                .desiredDeliveryTo(desiredDeliveryTo)
                .build();

        for (CreateOrderRequest.CreateOrderItem item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> {
                        log.error("Product not found with id={}", item.productId());
                        return new NotFoundException(item.productId(), "محصول");
                    });

            if (product.getQuantity() < item.quantity()) {
                log.warn("Insufficient stock for product={} (requested={}, available={})",
                        product.getName(), item.quantity(), product.getQuantity());
                throw new InsufficientStockException(product.getName(), product.getQuantity());
            }
            product.setQuantity(product.getQuantity() - item.quantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.quantity())
                    .price(product.getPrice())
                    .discountValue(item.discountValue())
                    .discountPercent(item.discountPercent())
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);

        order.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId, "مشتری")));

        calculateTotals(order);

        Order saved = repository.save(order);

        log.info("Order {} placed successfully", saved.getOrderNumber());

        eventPublisher.publish(new OrderPlacedEvent(UUID.randomUUID().toString(), saved));

        return saved;
    }

    @Transactional
    public Order cancelOrder(Long orderId, String reason){
        log.info("canceling order with id ",orderId);
        Order order = repository.findById(orderId)
                .orElseThrow(()-> {
                    log.error("Order not found with id={}", orderId);
                    return new NotFoundException(orderId , "سفارش");
                });

        if (order.getStatus() == OrderStatus.CANCELLED){
            log.error("Order has been already cancelled");
            throw new OrderAlreadyCancelledException(orderId);
        }
        for (OrderItem item: order.getItems()){
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);

        order.setCancelReason(reason);

        eventPublisher.publish(new OrderCancelledEvent(UUID.randomUUID().toString(), order));

        return repository.save(order);
    }

    @Transactional
    public Order updateOrder(Long orderId, List<CreateOrderRequest.CreateOrderItem> newItems,
                             Long customerId, BigDecimal orderDiscountValue,
                             BigDecimal orderDiscountPercent, LocalDateTime desiredDeliveryFrom,
                             LocalDateTime desiredDeliveryTo) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", orderId);
                    return new NotFoundException(orderId,"سفارش");
                });

        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.error("Order already has been cancelled");
            throw new DynamicTextException("سفارش کنسل شده است.");
        }

        List<OrderItem> oldItems = new ArrayList<>(order.getItems());

        for (OrderItem oldItem : order.getItems()) {
            Product product = oldItem.getProduct();
            product.setQuantity(product.getQuantity() + oldItem.getQuantity());
        }

        order.getItems().clear();

        for (CreateOrderRequest.CreateOrderItem newItem : newItems) {
            Product product = productRepository.findById(newItem.productId())
                    .orElseThrow(() -> new NotFoundException(newItem.productId() ,"محصول"));

            if (product.getQuantity() < newItem.quantity()) {
                throw new InsufficientStockException(product.getName() , product.getQuantity());
            }

            product.setQuantity(product.getQuantity() - newItem.quantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(newItem.quantity())
                    .price(product.getPrice())
                    .discountValue(newItem.discountValue())
                    .discountPercent(newItem.discountPercent())
                    .build();

            order.getItems().add(orderItem);
        }

        order.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId, "مشتری")));

        order.setOrderDiscountValue(orderDiscountValue);
        order.setOrderDiscountPercent(orderDiscountPercent);
        order.setDesiredDeliveryFrom(desiredDeliveryFrom);
        order.setDesiredDeliveryTo(desiredDeliveryTo);

        calculateTotals(order);
        Order saved = repository.save(order);

        eventPublisher.publish(new OrderUpdatedEvent(UUID.randomUUID().toString(),orderId, saved, oldItems));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersFiltered(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return repository.findFiltered(status, from, to, pageable);
    }

    @Transactional(readOnly = true)
    public Order findOrder(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId, "سفارش"));
    }


    //event driven methods
    @Transactional
    public void cancelOrdersContainingProduct(Long productId) {
        List<Order> affectedOrders = repository.findByProductIdAndStatus(productId, OrderStatus.PLACED);

        for (Order order : affectedOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            repository.save(order);
            eventPublisher.publish(new OrderCancelledEvent(UUID.randomUUID().toString(), order));
        }
    }
    private void calculateTotals(Order order) {
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            BigDecimal linePrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            // تخفیف آیتم (اگر مبلغ ثابت)
            if (item.getDiscountValue() != null) {
                linePrice = linePrice.subtract(item.getDiscountValue());
            }
            // تخفیف درصدی
            else if (item.getDiscountPercent() != null) {
                BigDecimal percent = item.getDiscountPercent().divide(BigDecimal.valueOf(100));
                linePrice = linePrice.subtract(linePrice.multiply(percent));
            }

            subTotal = subTotal.add(linePrice);
        }

        // تخفیف کلی Order
        if (order.getOrderDiscountValue() != null) {
            subTotal = subTotal.subtract(order.getOrderDiscountValue());
        }
        if (order.getOrderDiscountPercent() != null) {
            BigDecimal percent = order.getOrderDiscountPercent().divide(BigDecimal.valueOf(100));
            subTotal = subTotal.subtract(subTotal.multiply(percent));
        }

        if (subTotal.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Miscalculation happened: negative subtotal");
            throw new DynamicTextException("در محاسبه مبلغ سفارش خطایی رخ داد!");
        }

        order.setSubTotal(subTotal);

        BigDecimal tax = subTotal.multiply(BigDecimal.valueOf(0.09)); // مثال: ۹٪
        order.setTaxAmount(tax);
        order.setTotalAmount(subTotal.add(tax));
    }


    @Transactional
    public void updatePendingOrdersWithNewProductInfo(Product product) {
        List<Order> pendingOrders = repository.findByProductIdAndStatus(product.getId(), OrderStatus.PLACED);

        for (Order order : pendingOrders) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getId() == product.getId()) {
                    item.setProduct(product);
                }
            }
            repository.save(order);
        }
    }

    @Transactional
    public Order confirmOrder(Long id){
        Order order = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای تایید نیست.");
        }

        order.setStatus(OrderStatus.CONFIRMED);

        Order saved = repository.save(order);
        eventPublisher.publish(new OrderConfirmedEvent(UUID.randomUUID().toString(), saved));
        return saved;
    }

    @Transactional
    public Order markAsPaid(Long id, BigDecimal amount){
        Order order = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای پرداخت نیست.");
        }

        if (amount.compareTo(order.getTotalAmount()) != 0) {
            throw new DynamicTextException("مبلغ پرداخت شده با مبلغ سفارش مطابقت ندارد.");
        }

        order.setStatus(OrderStatus.PAID);

        Order saved = repository.save(order);
        eventPublisher.publish(new OrderPaidEvent(UUID.randomUUID().toString(), saved, amount));
        return saved;
    }

    @Transactional
    public Order shipOrder(Long id, String trackingCode){
        Order order = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.PAID) {
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای ارسال نیست.");
        }

        order.setStatus(OrderStatus.SHIPPED);

        Order saved = repository.save(order);
        eventPublisher.publish(new OrderShippedEvent(UUID.randomUUID().toString(), saved, trackingCode));
        return saved;
    }

}