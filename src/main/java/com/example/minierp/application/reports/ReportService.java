package com.example.minierp.application.reports;

import com.example.minierp.domain.product.ProductRepository;
import com.example.minierp.domain.reports.ReportCriteria;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.example.minierp.application.common.utils.PdfUtils.*;
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

        doc.add(createFarsiParagraphTable("گزارش فروش",  FARSI_TITLE_FONT, Element.ALIGN_CENTER));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        addTableHeader(table, "سفارش#", "مشتری", "وضعیت", "جمع جزء", "مالیات", "جمع کل", "تاریخ ایجاد");

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
            table.addCell(createFarsiCell(o.getStatus().getFarsiName(), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getSubTotal()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getTaxAmount()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(formatAmount(o.getTotalAmount()), FARSI_NORMAL_FONT));
            table.addCell(createFarsiCell(toSolarDate(o.getCreatedAt()), FARSI_NORMAL_FONT));

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
        doc.add(createFarsiParagraphTable(" ",FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        doc.add(createFarsiParagraphTable("خلاصه سفارش:", FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        doc.add(createFarsiParagraphTable("جمع جزء کل: " + formatAmount(totalSub),FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        doc.add(createFarsiParagraphTable("مالیات کل: " + formatAmount(totalTax), FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        doc.add(createFarsiParagraphTable("مبلغ کل: " + formatAmount(totalAmount), FARSI_BOLD_FONT, Element.ALIGN_LEFT));
    }
    // ----------------- INVENTORY -----------------
    private void generateInventoryReport(Document doc, ReportCriteria criteria) throws DocumentException {
        var products = criteria.productId() != null ?
                productRepository.findById(criteria.productId()).stream().toList() :
                productRepository.findAll();

        doc.add(createFarsiParagraphTable("گزارش موجودی کالا", FARSI_BOLD_FONT, Element.ALIGN_CENTER));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT, Element.ALIGN_CENTER));


        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addTableHeader(table, "کالا", "دسته‌بندی", "تعداد موجودی", "آستانه کمبود", "قیمت واحد");

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

        doc.add(createFarsiParagraphTable("گزارش محصولات", FARSI_BOLD_FONT, Element.ALIGN_CENTER));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT, Element.ALIGN_CENTER));


        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addTableHeader(table, "نام محصول", "قیمت", "دسته‌بندی");

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

        doc.add(createFarsiParagraphTable("گزارش دسته‌بندی‌ها", FARSI_BOLD_FONT, Element.ALIGN_CENTER));
        doc.add(createFarsiParagraphTable(" ", FARSI_NORMAL_FONT, Element.ALIGN_CENTER));


        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addTableHeader(table, "نام دسته‌بندی", "تعداد محصولات");

        for (var c : categories) {
            table.addCell(createFarsiCell(c.getName(), FARSI_NORMAL_FONT));
            long count = productRepository.findByCategoryId(c.getId()).size();
            table.addCell(createFarsiCell(String.valueOf(count), FARSI_NORMAL_FONT));
        }
        doc.add(table);

    }

    // helper methods

}
