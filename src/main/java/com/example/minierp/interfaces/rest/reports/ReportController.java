package com.example.minierp.interfaces.rest.reports;

import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.sales.Order;
import com.example.minierp.domain.sales.OrderRepository;
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

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final PdfReportService pdfReportService;


    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    @GetMapping("/orders")
    public List<OrderReportResponse> getOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        List<Order> orders;
        if (from != null && to != null) {
            orders = orderRepo.findByCreatedAtBetween(from, to);
        } else {
            orders = orderRepo.findAll();
        }

        return orders.stream()
                .map(OrderReportResponse::from)
                .toList();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
    public List<LowStockResponse> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        return productRepo.findByQuantityLessThanEqual(threshold).stream()
                .map(p -> new LowStockResponse(p.getName(), p.getQuantity()))
                .toList();
    }
    @GetMapping("/orders/pdf")
    @PreAuthorize("hasRole('SALES') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadOrdersAsPdf() {
        List<Order> orders = orderRepo.findAll();
        ByteArrayInputStream bis = pdfReportService.generateOrderReport(orders);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=orders.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }

}

