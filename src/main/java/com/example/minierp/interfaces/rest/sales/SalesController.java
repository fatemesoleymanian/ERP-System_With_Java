package com.example.minierp.interfaces.rest.sales;


import com.example.minierp.api.common.ApiResponse;
import com.example.minierp.application.sales.SalesService;
import com.example.minierp.domain.sales.Order;
import com.example.minierp.domain.sales.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin
public class SalesController {

    private final SalesService service;

    @PostMapping("/order")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@RequestBody @Valid CreateOrderRequest request) {
        Order order = service.placeOrder(request.items(), request.customerId(), request.orderDiscountValue(), request.orderDiscountPercent(), request.desiredDeliveryFrom(), request.desiredDeliveryTo());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(toResponse(order)));
    }

    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId, @RequestParam String reason){
        Order order = service.cancelOrder(orderId, reason);
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable Long orderId,
            @RequestBody @Valid CreateOrderRequest request){

        Order order = service.updateOrder(orderId, request.items(), request.customerId(), request.orderDiscountValue(), request.orderDiscountPercent(), request.desiredDeliveryFrom(), request.desiredDeliveryTo());
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    @PutMapping("/confirm/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(@PathVariable Long orderId) {
        Order order = service.confirmOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    @PutMapping("/pay/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> markAsPaid(
            @PathVariable Long orderId,
            @RequestParam BigDecimal amount) {
        Order order = service.markAsPaid(orderId, amount);
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    @PutMapping("/ship/{orderId}")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> shipOrder(
            @PathVariable Long orderId,
            @RequestParam String trackingCode) {
        Order order = service.shipOrder(orderId, trackingCode);
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAll(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        LocalDateTime fromDateTime = from != null ? LocalDateTime.parse(from) : null;
        LocalDateTime toDateTime = to != null ? LocalDateTime.parse(to) : null;

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders = service.getOrdersFiltered(status, fromDateTime, toDateTime, pageable);

        Page<OrderResponse> responsePage = orders.map(this::toResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> findById(@PathVariable long id){
        Order order = service.findOrder(id);
        return ResponseEntity.ok(ApiResponse.success(toResponse(order)));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderResponseItem> itemDtos = order.getItems().stream()
                .map(i -> new OrderResponse.OrderResponseItem(
                        i.getProduct().getName(),
                        i.getQuantity(),
                        i.getPrice(),
                        i.getDiscountValue(),
                        i.getDiscountPercent()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getVersion(),
                itemDtos,
                order.getSubTotal(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getOrderDiscountValue(),
                order.getOrderDiscountPercent(),
                order.getCancelReason(),
                order.getCustomer().getName(),
                order.getDesiredDeliveryFrom(),
                order.getDesiredDeliveryTo()
        );
    }
}