package com.example.minierp.application.product;

import com.example.minierp.domain.product.Product;
import com.example.minierp.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductExportService {

    private final ProductRepository productRepository;

    public ByteArrayInputStream exportProductsToExcel() throws IOException {
        List<Product> products = productRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("شناسه");
            header.createCell(1).setCellValue("نام");
            header.createCell(2).setCellValue("قیمت");
            header.createCell(3).setCellValue("SKU");
            header.createCell(4).setCellValue("موجودی");
            header.createCell(5).setCellValue("دسته بندی");
            header.createCell(6).setCellValue("تخفیف مبلغ");
            header.createCell(7).setCellValue("تخفیف درصد");

            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getPrice().doubleValue());
                row.createCell(3).setCellValue(product.getSku());
                row.createCell(4).setCellValue(product.getQuantity());
                row.createCell(5).setCellValue(product.getCategory().getName());
                row.createCell(6).setCellValue(product.getDiscountValue().doubleValue());
                row.createCell(7).setCellValue(product.getDiscountPercentage());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
