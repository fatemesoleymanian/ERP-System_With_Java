package com.example.minierp.application.sales;

import com.example.minierp.domain.common.exceptions.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleOrderService {

    private final ProductRepository productRepository;
    private final SaleOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final DomainEventPublisher eventPublisher;

    // ---------------------- CREATE ----------------------
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder placeOrder(List<CreateOrderRequest.CreateOrderItem> items,
                                Long customerId,
                                BigDecimal orderDiscountValue,
                                BigDecimal orderDiscountPercent,
                                LocalDateTime desiredDeliveryFrom,
                                LocalDateTime desiredDeliveryTo) {

        log.info("Placing new order with {} items for customer={}", items.size(), customerId);

        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId, "مشتری"));

        List<OrderItem> orderItems = new ArrayList<>();
        SaleOrder order = SaleOrder.builder()
                .orderNumber(UUID.randomUUID().toString())
                .status(OrderStatus.PLACED)
                .orderDiscountValue(orderDiscountValue)
                .orderDiscountPercent(orderDiscountPercent)
                .desiredDeliveryFrom(desiredDeliveryFrom)
                .desiredDeliveryTo(desiredDeliveryTo)
                .customer(customer)
                .build();

        for (var item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException(item.productId(), "محصول"));

            if (product.getQuantity() < item.quantity())
                throw new InsufficientStockException(product.getName(), product.getQuantity());

            product.setQuantity(product.getQuantity() - item.quantity());

            orderItems.add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.quantity())
                    .price(product.getPrice())
                    .discountValue(item.discountValue())
                    .discountPercent(item.discountPercent())
                    .build());
        }

        order.setItems(orderItems);
        calculateTotals(order);

        SaleOrder saved = orderRepository.save(order);
        eventPublisher.publish(new OrderPlacedEvent(UUID.randomUUID().toString(), saved));

        /** TODO inventory has a problem after order placed **/
        log.info("✅ Order {} placed successfully", saved.getOrderNumber());
        return saved;
    }

    // ---------------------- UPDATE ----------------------
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder updateOrder(Long orderId,
                                 List<CreateOrderRequest.CreateOrderItem> newItems,
                                 Long customerId,
                                 BigDecimal orderDiscountValue,
                                 BigDecimal orderDiscountPercent,
                                 LocalDateTime desiredDeliveryFrom,
                                 LocalDateTime desiredDeliveryTo) {

        SaleOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId, "سفارش"));

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new DynamicTextException("سفارش کنسل شده است.");

        List<OrderItem> oldItems = new ArrayList<>(order.getItems());

        // بازگرداندن موجودی محصولات قبلی
        oldItems.forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
        });

        order.getItems().clear();

        for (var newItem : newItems) {
            Product product = productRepository.findById(newItem.productId())
                    .orElseThrow(() -> new NotFoundException(newItem.productId(), "محصول"));

            if (product.getQuantity() < newItem.quantity())
                throw new InsufficientStockException(product.getName(), product.getQuantity());

            product.setQuantity(product.getQuantity() - newItem.quantity());

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(newItem.quantity())
                    .price(product.getPrice())
                    .discountValue(newItem.discountValue())
                    .discountPercent(newItem.discountPercent())
                    .build());
        }

        order.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId, "مشتری")));
        order.setOrderDiscountValue(orderDiscountValue);
        order.setOrderDiscountPercent(orderDiscountPercent);
        order.setDesiredDeliveryFrom(desiredDeliveryFrom);
        order.setDesiredDeliveryTo(desiredDeliveryTo);

        calculateTotals(order);
        SaleOrder saved = orderRepository.save(order);

        eventPublisher.publish(new OrderUpdatedEvent(UUID.randomUUID().toString(), orderId, saved, oldItems));
        log.info("✅ Order {} updated successfully", saved.getOrderNumber());
        return saved;
    }

    // ---------------------- CANCEL ----------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder cancelOrder(Long orderId, String reason) {
        SaleOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId, "سفارش"));

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new OrderAlreadyCancelledException(orderId);

        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
        });

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelReason(reason);

        SaleOrder saved = orderRepository.save(order);

        // You can log it and save it in db for tracking order
        eventPublisher.publish(new OrderCancelledEvent(UUID.randomUUID().toString(), saved, reason));

        log.info("🚫 Order {} cancelled", orderId);
        return saved;
    }

    // ---------------------- STATUS TRANSITIONS ----------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder confirmOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.PLACED)
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای تایید نیست.");

        order.setStatus(OrderStatus.CONFIRMED);
        SaleOrder saved = orderRepository.save(order);

        eventPublisher.publish(new OrderConfirmedEvent(UUID.randomUUID().toString(), saved));
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder markAsPaid(Long id, BigDecimal amount) {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.CONFIRMED)
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای پرداخت نیست.");

        if (amount.compareTo(order.getTotalAmount()) != 0)
            throw new DynamicTextException("مبلغ پرداختی با مبلغ سفارش مطابقت ندارد.");

        order.setStatus(OrderStatus.PAID);
        SaleOrder saved = orderRepository.save(order);

        eventPublisher.publish(new OrderPaidEvent(UUID.randomUUID().toString(), saved, amount));
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder shipOrder(Long id, String trackingCode) {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.PAID)
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای ارسال نیست.");

        order.setStatus(OrderStatus.SHIPPED);
        SaleOrder saved = orderRepository.save(order);

        eventPublisher.publish(new OrderShippedEvent(UUID.randomUUID().toString(), saved, trackingCode));
        return saved;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public SaleOrder completeOrder(Long id) {
        SaleOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "سفارش"));

        if (order.getStatus() != OrderStatus.SHIPPED)
            throw new DynamicTextException("سفارش در وضعیت مناسبی برای تکمیل نیست.");

        order.setStatus(OrderStatus.COMPLETED);
        SaleOrder saved = orderRepository.save(order);

        eventPublisher.publish(new OrderCompletedEvent(UUID.randomUUID().toString(), saved));
        log.info("✅ Order {} completed", saved.getOrderNumber());
        return saved;
    }
    // ---------------------- QUERY METHODS ----------------------
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public List<SaleOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public Page<SaleOrder> getOrdersFiltered(OrderStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return orderRepository.findFiltered(status, from, to, pageable);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public SaleOrder findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId, "سفارش"));
    }

    // ---------------------- EVENT HANDLING HELPERS ----------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void cancelOrdersContainingProduct(Long productId) {
        List<SaleOrder> orders = orderRepository.findByProductIdAndStatus(productId, OrderStatus.PLACED);
        for (SaleOrder order : orders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            eventPublisher.publish(new OrderCancelledEvent(UUID.randomUUID().toString(), order, "محصول حذف شده است"));
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updatePendingOrdersWithNewProductInfo(Product product) {
        List<SaleOrder> pendingOrders = orderRepository.findByProductIdAndStatus(product.getId(), OrderStatus.PLACED);
        for (SaleOrder order : pendingOrders) {
            order.getItems().forEach(item -> {
                if (item.getProduct().getId().equals(product.getId())) {
                    item.setProduct(product);
                }
            });
            orderRepository.save(order);
        }
    }

    // ---------------------- UTILITIES ----------------------
    private void calculateTotals(SaleOrder order) {
        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            if (item.getDiscountValue() != null)
                lineTotal = lineTotal.subtract(item.getDiscountValue());
            else if (item.getDiscountPercent() != null)
                lineTotal = lineTotal.subtract(lineTotal.multiply(item.getDiscountPercent().divide(BigDecimal.valueOf(100))));

            subTotal = subTotal.add(lineTotal);
        }

        if (order.getOrderDiscountValue() != null)
            subTotal = subTotal.subtract(order.getOrderDiscountValue());
        if (order.getOrderDiscountPercent() != null)
            subTotal = subTotal.subtract(subTotal.multiply(order.getOrderDiscountPercent().divide(BigDecimal.valueOf(100))));

        if (subTotal.compareTo(BigDecimal.ZERO) < 0)
            throw new DynamicTextException("در محاسبه مبلغ سفارش خطایی رخ داد!");

        order.setSubTotal(subTotal);
        BigDecimal tax = subTotal.multiply(BigDecimal.valueOf(0.09));
        order.setTaxAmount(tax);
        order.setTotalAmount(subTotal.add(tax));
    }
}
