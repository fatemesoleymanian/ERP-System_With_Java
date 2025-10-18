package com.example.minierp.application.reports;

import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.reports.ReportCriteria;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleOrderRepository saleOrderRepository;
    private final ProductRepository productRepository;

    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

    private static BaseFont FARSI_BASE_FONT;
    private static Font FARSI_NORMAL_FONT;
    private static Font FARSI_BOLD_FONT;

    static {
        try {
            FARSI_BASE_FONT = BaseFont.createFont("fonts/Vazir.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            FARSI_NORMAL_FONT = new Font(FARSI_BASE_FONT, 10, Font.NORMAL);


            FARSI_BOLD_FONT = new Font(FARSI_BASE_FONT, 18, Font.BOLD);

        } catch (DocumentException | IOException e) {
            System.err.println("FATAL: Could not load Farsi font! " + e.getMessage());
            FARSI_NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            FARSI_BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

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

        doc.add(createFarsiParagraphTable("گزارش فروش", FARSI_BOLD_FONT));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        addHeader(table, "سفارش#", "مشتری", "وضعیت", "جمع جزء", "مالیات", "جمع کل", "تاریخ ایجاد");

        BigDecimal totalSub = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleOrder o : orders) {
//            table.addCell(o.getOrderNumber());
//            table.addCell(o.getCustomer().getName());
//            table.addCell(o.getStatus().name());
//            table.addCell(formatAmount(o.getSubTotal()));
//            table.addCell(formatAmount(o.getTaxAmount()));
//            table.addCell(formatAmount(o.getTotalAmount()));
//            table.addCell(o.getCreatedAt().toString());

            table.addCell(createFarsiCell(o.getOrderNumber(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(o.getCustomer().getName(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(o.getStatus().name(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getSubTotal()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getTaxAmount()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getTotalAmount()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(o.getCreatedAt().toString(), FARSI_NORMAL_FONT));

            totalSub = totalSub.add(o.getSubTotal());
            totalTax = totalTax.add(o.getTaxAmount());
            totalAmount = totalAmount.add(o.getTotalAmount());
        }
        doc.add(table);

//        doc.add(new Paragraph(" "));
//        doc.add(new Paragraph("خلاصه سفارش:"));
//        doc.add(new Paragraph("جمع جزء کل: " + formatAmount(totalSub)));
//        doc.add(new Paragraph("مالیات کل: " + formatAmount(totalTax)));
//        doc.add(new Paragraph("مبلغ کل: " + formatAmount(totalAmount)));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT));
        doc.add(createFarsiParagraphTable("خلاصه سفارش:", FARSI_NORMAL_FONT));
        doc.add(createFarsiParagraphTable("جمع جزء کل: " + formatAmount(totalSub), FARSI_NORMAL_FONT));
        doc.add(createFarsiParagraphTable("مالیات کل: " + formatAmount(totalTax), FARSI_NORMAL_FONT));
        doc.add(createFarsiParagraphTable("مبلغ کل: " + formatAmount(totalAmount), FARSI_NORMAL_FONT));
    }
    // ----------------- INVENTORY -----------------
    private void generateInventoryReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var products = criteria.productId() != null ?
                productRepository.findById(criteria.productId()).stream().toList() :
                productRepository.findAll();

        doc.add(createFarsiParagraphTable("گزارش موجودی کالا", FARSI_BOLD_FONT));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT));


        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addHeader(table, "کالا", "دسته‌بندی", "تعداد موجودی", "آستانه کمبود", "قیمت واحد");

        int lowStockThreshold = 5;

        for (var p : products) {
            table.addCell(createFarsiCell(p.getName(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(p.getCategory() != null ? p.getCategory().getName() : "-", FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(String.valueOf(p.getQuantity()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(p.getQuantity() < lowStockThreshold ? "⚠ کمتر از " + lowStockThreshold : String.valueOf(lowStockThreshold), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(p.getPrice()), FARSI_NORMAL_FONT));
        }
        doc.add(table);
    }

    // ----------------- PRODUCT -----------------
    private void generateProductReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var products = productRepository.findAll();
        if(criteria.categoryId() != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(criteria.categoryId()))
                    .toList();
        }

        doc.add(createFarsiParagraphTable("گزارش محصولات", FARSI_BOLD_FONT));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT));


        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addHeader(table, "نام محصول", "قیمت", "دسته‌بندی");

        for (var p : products) {
            table.addCell(createFarsiCell(p.getName(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(p.getPrice()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(p.getCategory() != null ? p.getCategory().getName() : "-", FARSI_NORMAL_FONT));
        }
        doc.add(table);

    }

    // ----------------- CATEGORY -----------------
    private void generateCategoryReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var categories = productRepository.findAll().stream()
                .map(p -> p.getCategory())
                .distinct()
                .toList();

        doc.add(createFarsiParagraphTable("گزارش دسته‌بندی‌ها", FARSI_BOLD_FONT));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT));


        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addHeader(table, "نام دسته‌بندی", "تعداد محصولات");

        for (var c : categories) {
            table.addCell(createFarsiCell(c.getName(), FARSI_NORMAL_FONT));
            long count = productRepository.findByCategoryId(c.getId()).size();
            table.addCell(createFarsiCell(String.valueOf(count), FARSI_NORMAL_FONT));
        }
        doc.add(table);

    }

    // helper methods
    private void addHeader(PdfPTable table, String... headers) {
        Font headerFont = new Font(FARSI_BASE_FONT, 10, Font.BOLD);

        for (String h : headers) {
            PdfPCell cell = createFarsiCell(h, headerFont);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private PdfPTable createFarsiParagraphTable(String text, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        table.setSpacingBefore(5f);

        PdfPCell cell = createFarsiCell(text, font);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0f);

        table.addCell(cell);
        return table;
    }
    private PdfPCell createFarsiCell(String text, Font font) {
        Chunk chunk = new Chunk(text, font);
        Phrase phrase = new Phrase();
        phrase.add(chunk);

        PdfPCell cell = new PdfPCell(phrase);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private String formatAmount(BigDecimal amount) {
        return numberFormat.format(amount);
    }

}
