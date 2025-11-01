package com.example.minierp.interfaces.rest.reports;

import com.example.minierp.application.reports.FinancialReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports/financial")
@RequiredArgsConstructor
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/seasonal-sales")
    public ResponseEntity<InputStreamResource> getSeasonalSalesReport(
            @RequestParam int year,
            @RequestParam int quarter) throws IOException {

        ByteArrayInputStream excelFile = financialReportService.generateSeasonalSalesReport(year, quarter);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=seasonal_report_" + year + "_Q" + quarter + ".xlsx");

        String excelMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(excelMimeType)) // استفاده از نوع MIME دقیق
                .body(new InputStreamResource(excelFile));
    }
}