package com.example.minierp.interfaces.rest.reports;

import com.example.minierp.application.reports.ReportService;
import com.example.minierp.domain.reports.ReportCriteria;
import com.lowagie.text.DocumentException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody ReportCriteria criteria) throws DocumentException {
        byte[] pdf = reportService.generateReport(criteria);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", criteria.type().name() + "-report.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
