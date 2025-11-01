package com.example.minierp.application.reports;

import com.example.minierp.domain.sales.SaleOrder;
import com.example.minierp.domain.sales.SaleOrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final SaleOrderRepository saleOrderRepository;

    public ByteArrayInputStream generateSeasonalSalesReport(int year, int quarter) throws IOException {
        LocalDate startDate = getStartDateOfQuarter(year, quarter);
        LocalDate endDate = getEndDateOfQuarter(year, quarter);

        List<SaleOrder> orders = saleOrderRepository.findAllByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("گزارش فروش فصلی");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ردیف", "شماره فاکتور", "تاریخ", "نام خریدار", "شماره اقتصادی خریدار", "مبلغ کل", "مالیات بر ارزش افزوده", "مبلغ خالص"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Data Rows
            int rowIdx = 1;
            for (SaleOrder order : orders) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(order.getOrderNumber());
                row.createCell(2).setCellValue(order.getCreatedAt().toLocalDate().toString());
                row.createCell(3).setCellValue(order.getCustomer().getName());
                row.createCell(4).setCellValue(order.getCustomer().getEconomicCode()); // فرض می‌کنیم این فیلد به Customer اضافه شده
                row.createCell(5).setCellValue(order.getTotalAmount().doubleValue());
                row.createCell(6).setCellValue(order.getTaxAmount().doubleValue());
                row.createCell(7).setCellValue(order.getSubTotal().doubleValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private LocalDate getStartDateOfQuarter(int year, int quarter) {
        Month startMonth = Month.of((quarter - 1) * 3 + 1);
        return LocalDate.of(year, startMonth, 1);
    }

    private LocalDate getEndDateOfQuarter(int year, int quarter) {
        Month startMonth = Month.of((quarter - 1) * 3 + 1);
        return LocalDate.of(year, startMonth.plus(2), startMonth.plus(2).maxLength());
    }
}