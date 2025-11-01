package com.example.minierp.application.reports;

import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static com.example.minierp.application.common.utils.PdfUtils.*;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final SaleOrderRepository saleOrderRepository;

    public byte[] generateInvoice(Long orderId) throws DocumentException {
        SaleOrder order = saleOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(orderId, "سفارش"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // --- HEADER ---
        document.add(createFarsiParagraphTable("فاکتور فروش", FARSI_TITLE_FONT, Element.ALIGN_CENTER));
        document.add(new Paragraph(" "));

        document.add(createFarsiParagraphTable("شماره سفارش:" + order.getOrderNumber(), FARSI_NORMAL_FONT, Element.ALIGN_LEFT));
        document.add(createFarsiParagraphTable("تاریخ:" + toSolarDate(order.getCreatedAt()), FARSI_NORMAL_FONT, Element.ALIGN_LEFT));
        document.add(createFarsiParagraphTable("نام خریدار:" + order.getCustomer().getName(), FARSI_NORMAL_FONT, Element.ALIGN_LEFT));
        document.add(createFarsiParagraphTable("آدرس:" + order.getCustomer().getBillingAddress(), FARSI_NORMAL_FONT, Element.ALIGN_LEFT));

        document.add(new Paragraph(" "));

        // --- ORDER ITEMS TABLE ---
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        addTableHeader(itemsTable, "شرح کالا/خدمات", "تعداد", "قیمت واحد", "تخفیف", "مبلغ کل");

        order.getItems().forEach(item -> {
            BigDecimal lineTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            BigDecimal discount = item.getDiscountValue() != null ? item.getDiscountValue() : BigDecimal.ZERO;
            if (item.getDiscountPercent() != null) {
                discount = discount.add(lineTotal.multiply(item.getDiscountPercent().divide(new BigDecimal(100))));
            }
            itemsTable.addCell(createFarsiCell(item.getProduct().getName(), FARSI_NORMAL_FONT));
            itemsTable.addCell(createFarsiCell(String.valueOf(item.getQuantity()), FARSI_NORMAL_FONT));
            itemsTable.addCell(createFarsiCell(formatAmount(item.getPrice()), FARSI_NORMAL_FONT));
            itemsTable.addCell(createFarsiCell(formatAmount(discount), FARSI_NORMAL_FONT));
            itemsTable.addCell(createFarsiCell(formatAmount(lineTotal.subtract(discount)), FARSI_NORMAL_FONT));
        });
        document.add(itemsTable);
        document.add(new Paragraph(" "));

        // --- TOTALS ---
        document.add(createFarsiParagraphTable("جمع کل: " + formatAmount(order.getSubTotal()), FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        document.add(createFarsiParagraphTable("مالیات بر ارزش افزوده: " + formatAmount(order.getTaxAmount()), FARSI_BOLD_FONT, Element.ALIGN_LEFT));
        document.add(createFarsiParagraphTable("مبلغ قابل پرداخت: " + formatAmount(order.getTotalAmount()), FARSI_BOLD_FONT, Element.ALIGN_LEFT));

        document.close();
        return baos.toByteArray();
    }
}