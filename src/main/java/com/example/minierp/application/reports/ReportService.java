package com.example.minierp.application.reports;

import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.reports.ReportCriteria;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleOrderRepository saleOrderRepository;
    private final ProductRepository productRepository;

    public byte[] generateReport(ReportCriteria criteria) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, baos);
        document.open();

        switch (criteria.type()) {
            case SALES -> generateSalesReport(document, criteria);
            case INVENTORY -> generateInventoryReport(document, criteria);
            case PRODUCT -> generateProductReport(document, criteria);
            case CATEGORY -> generateCategoryReport(document, criteria);
        }

        document.close();
        return baos.toByteArray();
    }

    // ----------------- SALES -----------------
    private void generateSalesReport(Document doc, ReportCriteria criteria) throws DocumentException {
        List<SaleOrder> orders = saleOrderRepository.findFiltered(
                criteria.orderStatus(),
                criteria.from(),
                criteria.to(),
                null // paging ignored
        ).getContent();

        if(criteria.customerId() != null) {
            orders = orders.stream()
                    .filter(o -> o.getCustomer().getId().equals(criteria.customerId()))
                    .toList();
        }

        if(criteria.productId() != null) {
            orders = orders.stream()
                    .filter(o -> o.getItems().stream().anyMatch(i -> i.getProduct().getId().equals(criteria.productId())))
                    .toList();
        }

        Paragraph title = new Paragraph("Sales Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        addHeader(table, "Order#", "Customer", "Status", "SubTotal", "Tax", "Total", "Created At");

        BigDecimal totalSub = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleOrder o : orders) {
            table.addCell(o.getOrderNumber());
            table.addCell(o.getCustomer().getName());
            table.addCell(o.getStatus().name());
            table.addCell(o.getSubTotal().toString());
            table.addCell(o.getTaxAmount().toString());
            table.addCell(o.getTotalAmount().toString());
            table.addCell(o.getCreatedAt().toString());

            totalSub = totalSub.add(o.getSubTotal());
            totalTax = totalTax.add(o.getTaxAmount());
            totalAmount = totalAmount.add(o.getTotalAmount());
        }
        doc.add(table);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Summary:"));
        doc.add(new Paragraph("Total SubTotal: " + totalSub));
        doc.add(new Paragraph("Total Tax: " + totalTax));
        doc.add(new Paragraph("Total Amount: " + totalAmount));
    }

    // ----------------- INVENTORY -----------------
    private void generateInventoryReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var products = criteria.productId() != null ?
                productRepository.findById(criteria.productId()).stream().toList() :
                productRepository.findAll();

        Paragraph title = new Paragraph("Inventory Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addHeader(table, "Product", "Category", "Quantity", "Low Stock Threshold", "Price");

        int lowStockThreshold = 5;

        for (var p : products) {
            table.addCell(p.getName());
            table.addCell(p.getCategory() != null ? p.getCategory().getName() : "-");
            table.addCell(String.valueOf(p.getQuantity()));
            table.addCell(p.getQuantity() < lowStockThreshold ? "⚠ " + lowStockThreshold : String.valueOf(lowStockThreshold));
            table.addCell(p.getPrice().toString());
        }
    }

    // ----------------- PRODUCT -----------------
    private void generateProductReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var products = productRepository.findAll();
        if(criteria.categoryId() != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(criteria.categoryId()))
                    .toList();
        }

        Paragraph title = new Paragraph("Product Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addHeader(table, "Product", "Price", "Category");

        for (var p : products) {
            table.addCell(p.getName());
            table.addCell(p.getPrice().toString());
            table.addCell(p.getCategory() != null ? p.getCategory().getName() : "-");
        }
    }

    // ----------------- CATEGORY -----------------
    private void generateCategoryReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var categories = productRepository.findAll().stream()
                .map(p -> p.getCategory())
                .distinct()
                .toList();

        Paragraph title = new Paragraph("Category Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addHeader(table, "Category", "Number of Products");

        for (var c : categories) {
            table.addCell(c.getName());
            long count = productRepository.findByCategoryId(c.getId()).size();
            table.addCell(String.valueOf(count));
        }
    }

    private void addHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(cell);
        }
    }
}
