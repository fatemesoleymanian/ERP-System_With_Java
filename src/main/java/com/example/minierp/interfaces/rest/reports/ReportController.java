package com.example.minierp.interfaces.rest.reports;

import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.Order;
import com.example.minierp.domain.sales.OrderRepository;
import com.example.minierp.domain.sales.OrderStatus;
import com.example.minierp.infrastructure.report.pdf.PdfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

//    private final OrderRepository orderRepo;
//    private final ProductRepository productRepo;
//    private final PdfReportService pdfReportService;
//
//
//    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
//    @GetMapping("/orders")
//    public List<OrderReportResponse> getOrders(
//            @RequestParam(required = false) String from,
//            @RequestParam(required = false) String to,
//            @RequestParam(required = false) String status
//    ) {
//
//        List<Order> orders = filterOrders(from, to, status, null);
//        return orders.stream()
//                .map(OrderReportResponse::from)
//                .toList();
//    }
//
//    @GetMapping("/low-stock")
//    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
//    public List<LowStockResponse> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
//        return productRepo.findByQuantityLessThanEqual(threshold).stream()
//                .map(p -> new LowStockResponse(p.getName(), p.getQuantity()))
//                .toList();
//    }
//    @GetMapping("/orders/pdf")
//    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
//    public ResponseEntity<byte[]> downloadOrdersAsPdf(
//            @RequestParam(required = false) String from,
//            @RequestParam(required = false) String to,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Long productId
//    ) {
//        List<Order> orders = filterOrders(from, to, status, productId, pageable);
//        ByteArrayInputStream bis;
//
//        if (orders.isEmpty()) {
//            bis = pdfReportService.generateEmptyOrderReport("No orders found for the specified filters.");
//        } else {
//            bis = pdfReportService.generateOrderReport(orders);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "inline; filename=orders.pdf");
//
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(bis.readAllBytes());
//    }
//    private List<Order> filterOrders(String from, String to, String status, Long productId, Pageable pageable) {
//        LocalDateTime fromDateTime = null;
//        LocalDateTime toDateTime = null;
//        OrderStatus orderStatus = null;
//
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//        if (from != null) {
//            try {
//                fromDateTime = LocalDateTime.parse(from, dateTimeFormatter);
//            } catch (DateTimeParseException e) {
//                try {
//                    LocalDate fromDate = LocalDate.parse(from, dateFormatter);
//                    fromDateTime = fromDate.atStartOfDay();
//                } catch (DateTimeParseException ex) {
//                    throw new IllegalArgumentException("Invalid 'from' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-dd'T'HH:mm:ss'.");
//                }
//            }
//        }
//
//        if (to != null) {
//            try {
//                toDateTime = LocalDateTime.parse(to, dateTimeFormatter);
//            } catch (DateTimeParseException e) {
//                try {
//                    LocalDate toDate = LocalDate.parse(to, dateFormatter);
//                    toDateTime = toDate.atTime(LocalTime.MAX);
//                } catch (DateTimeParseException ex) {
//                    throw new IllegalArgumentException("Invalid 'to' date format. Use 'yyyy-MM-dd' or 'yyyy-MM-dd'T'HH:mm:ss'.");
//                }
//            }
//        }
//
//        if (status != null) {
//            try {
//                orderStatus = OrderStatus.valueOf(status.toUpperCase());
//            } catch (IllegalArgumentException e) {
//                throw new IllegalArgumentException("Invalid 'status'. Must be one of: PLACED, CANCELLED, PAID, SHIPPED, COMPLETED");
//            }
//        }
//        if (productId != null && productId <= 0) {
//            throw new IllegalArgumentException("Invalid 'productId'. Must be a positive number.");
//        }
//
//        if (productId != null && orderStatus != null) {
//            if (fromDateTime != null && toDateTime != null) {
//                return orderRepo.findByProductIdAndStatusAndCreatedAtBetween(productId, orderStatus, fromDateTime, toDateTime);
//            }
//            return orderRepo.findByProductIdAndStatus(productId, orderStatus);
//        } else if (productId != null) {
//            if (fromDateTime != null && toDateTime != null) {
//                return orderRepo.findByProductIdAndCreatedAtBetween(productId, fromDateTime, toDateTime);
//            }
//            return orderRepo.findByProductId(productId);
//        } else if (fromDateTime != null && toDateTime != null && orderStatus != null) {
//            return orderRepo.findByCreatedAtBetweenAndStatus(fromDateTime, toDateTime, orderStatus);
//        } else if (fromDateTime != null && toDateTime != null) {
//            return orderRepo.findByCreatedAtBetween(fromDateTime, toDateTime, pageable);
//        } else if (orderStatus != null) {
//            return orderRepo.findByStatus(orderStatus, pageable);
//        } else {
//            return orderRepo.findAll();
//        }
//    }
//

}

