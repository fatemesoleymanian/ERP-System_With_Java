package com.example.minierp.interfaces.rest.product;

import com.example.minierp.application.product.ProductExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/products/export")
@RequiredArgsConstructor
@CrossOrigin
public class ProductExportController {

    private final ProductExportService exportService;

    @GetMapping("/excel")
    @PreAuthorize("!hasRole('SALES')")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        ByteArrayInputStream excel = exportService.exportProductsToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(excel));
    }
}

