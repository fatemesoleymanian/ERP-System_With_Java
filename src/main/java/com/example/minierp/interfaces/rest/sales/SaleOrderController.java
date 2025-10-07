package com.example.minierp.interfaces.rest.sales;

import com.example.minierp.application.sales.SaleOrderService;
import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.domain.sales.SaleOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class SaleOrderController {

    private final SaleOrderService service;

    /** ✅ Create a new order */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody CreateOrderRequest request) {
        SaleOrder order = service.placeOrder(
                request.items(),
                request.customerId(),
                request.orderDiscountValue(),
                request.orderDiscountPercent(),
                request.desiredDeliveryFrom(),
                request.desiredDeliveryTo()
        );
        return ResponseEntity.ok(OrderResponseDto.fromEntity(order));
    }

    /** ✅ Update an existing order */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request) {
        SaleOrder updated = service.updateOrder(
                id,
                request.items(),
                request.customerId(),
                request.orderDiscountValue(),
                request.orderDiscountPercent(),
                request.desiredDeliveryFrom(),
                request.desiredDeliveryTo()
        );
        return ResponseEntity.ok(OrderResponseDto.fromEntity(updated));
    }

    /** ✅ Cancel an order */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long id,
                                                        @RequestParam String reason) {
        SaleOrder order = service.cancelOrder(id, reason);
        return ResponseEntity.ok(OrderResponseDto.fromEntity(order));
    }

    /** ✅ Confirm an order */
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> confirmOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderResponseDto.fromEntity(service.confirmOrder(id)));
    }

    /** ✅ Mark as paid */
    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> markAsPaid(@PathVariable Long id,
                                                       @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(OrderResponseDto.fromEntity(service.markAsPaid(id, amount)));
    }

    /** ✅ Ship order */
    @PutMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> shipOrder(@PathVariable Long id,
                                                      @RequestParam String trackingCode) {
        return ResponseEntity.ok(OrderResponseDto.fromEntity(service.shipOrder(id, trackingCode)));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES')")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderResponseDto.fromEntity(service.completeOrder(id)));
    }

    /** ✅ Get single order */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public ResponseEntity<OrderResponseDto> findOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderResponseDto.fromEntity(service.findOrder(id)));
    }

    /** ✅ Get paginated / filtered orders */
    @GetMapping /** TODO Bugiiii **/
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public Page<OrderResponseDto> getOrdersFiltered(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable
    ) {
        // جلوگیری از Sort اشتباه
        Pageable safePageable = pageable;
        if (pageable.getSort().isUnsorted() || pageable.getSort().toString().contains("[")) {
            safePageable = Pageable.ofSize(pageable.getPageSize()).withPage(pageable.getPageNumber());
        }

        return service.getOrdersFiltered(status, from, to, safePageable)
                .map(OrderResponseDto::fromEntity);
    }


    /** ✅ Get all orders (no pagination) */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES') or hasRole('VIEWER')")
    public List<OrderResponseDto> getAllOrders() {
        return service.getAllOrders().stream()
                .map(OrderResponseDto::fromEntity)
                .toList();
    }
}
