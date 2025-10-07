package com.example.minierp.application.sales;

import com.example.minierp.domain.customer.CustomerDeletedEvent;
import com.example.minierp.domain.customer.CustomerUpdatedEvent;
import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductDeletedEvent;
import com.example.minierp.domain.product.ProductUpdatedEvent;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.domain.sales.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesEventHandler {

    private final SaleOrderService saleOrderService;
    private final SaleOrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    /**
     * وقتی محصولی به‌روزرسانی می‌شود (مثلاً قیمت یا نامش تغییر کرده)
     * سفارش‌های در وضعیت PLACED باید sync شوند.
     */
    @EventListener
    @Transactional
    public void handleProductUpdated(ProductUpdatedEvent event) {
        Product updatedProduct = event.product();
        log.info("Handling ProductUpdatedEvent for product id={}", updatedProduct.getId());

        saleOrderService.updatePendingOrdersWithNewProductInfo(updatedProduct);
    }

    /**
     * اگر محصولی حذف شود، سفارش‌هایی که هنوز ارسال نشده‌اند باید لغو شوند.
     */
    @EventListener
    @Transactional
    public void handleProductDeleted(ProductDeletedEvent event) {
        Long productId = event.productId();
        log.info("Handling ProductDeletedEvent for product id={}", productId);

        saleOrderService.cancelOrdersContainingProduct(productId);
    }

    /**
     * اگر مشتری ویرایش شود (مثلاً نام یا آدرس)، در سفارش‌های باز (PLACED) باید sync شود.
     */
    @EventListener
    @Transactional
    public void handleCustomerUpdated(CustomerUpdatedEvent event) {
        var updatedCustomer = event.customer();
        log.info("Handling CustomerUpdatedEvent for customer id={}", updatedCustomer.getId());

        List<SaleOrder> activeOrders = orderRepository.findByCreatedAtBetweenAndStatus(
                updatedCustomer.getCreatedAt().minusYears(1),  // یا بازه مناسب‌تر
                updatedCustomer.getCreatedAt().plusYears(1),
                OrderStatus.PLACED
        );

        for (SaleOrder order : activeOrders) {
            if (order.getCustomer().getId().equals(updatedCustomer.getId())) {
                order.setCustomer(updatedCustomer);
                orderRepository.save(order);
            }
        }
    }

    /**
     * اگر مشتری حذف شود، تمام سفارش‌های باز او باید لغو شوند.
     */
    @EventListener
    @Transactional
    public void handleCustomerDeleted(CustomerDeletedEvent event) {
        Long customerId = event.id(); // یا event.customerId() بسته به تعریف record
        log.info("Handling CustomerDeletedEvent for customer id={}", customerId);

        // پیدا کردن تمام سفارش‌های در حال پردازش مربوط به مشتری حذف‌شده
        List<SaleOrder> activeOrders = orderRepository.findByStatus(OrderStatus.PLACED)
                .stream()
                .filter(o -> o.getCustomer().getId().equals(customerId))
                .toList();

        for (SaleOrder order : activeOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelReason("مشتری حذف شد");
            orderRepository.save(order);

            eventPublisher.publish(
                    new OrderCancelledEvent(UUID.randomUUID().toString(), order, "مشتری حذف شد")
            );
        }

        log.info("Cancelled {} active orders for deleted customer {}", activeOrders.size(), customerId);
    }

}
