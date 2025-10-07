package com.example.minierp.infrastructure.report.pdf;

import com.example.minierp.domain.sales.OrderItem;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {

//    public ByteArrayInputStream generateOrderReport(List<Order> orders) {
//        Document document = new Document();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        try {
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            document.addTitle("Order Report");
//            Font headerFont = new Font(Font.HELVETICA, 16, Font.BOLD);
//            Font bodyFont = new Font(Font.HELVETICA, 12);
//
//            for (Order order : orders) {
//                Paragraph title = new Paragraph("Order #" + order.getOrderNumber(), headerFont);
//                document.add(title);
//                document.add(new Paragraph("Created at: " + order.getCreatedAt(), bodyFont));
//                document.add(new Paragraph("Status: " + order.getStatus(), bodyFont));
//                document.add(new Paragraph(" "));
//
//                for (OrderItem item : order.getItems()) {
//                    String line = "- " + item.getProduct().getName() +
//                            " | Qty: " + item.getQuantity() +
//                            " | Price: " + item.getPrice();
//                    document.add(new Paragraph(line, bodyFont));
//                }
//
//                document.add(new Paragraph("-----------------------------------------------------"));
//            }
//
//            document.close();
//        } catch (Exception e) {
//            throw new RuntimeException("Error generating PDF", e);
//        }
//
//        return new ByteArrayInputStream(out.toByteArray());
//    }
//    public ByteArrayInputStream generateEmptyOrderReport(String message) {
//        Document document = new Document();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        try {
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            document.addTitle("Order Report");
//            Font bodyFont = new Font(Font.HELVETICA, 12);
//
//            Paragraph messageParagraph = new Paragraph(message, bodyFont);
//            messageParagraph.setAlignment(Paragraph.ALIGN_CENTER);
//            document.add(messageParagraph);
//
//            document.close();
//        } catch (Exception e) {
//            throw new RuntimeException("Error generating empty PDF report", e);
//        }
//
//        return new ByteArrayInputStream(out.toByteArray());
//    }
}

