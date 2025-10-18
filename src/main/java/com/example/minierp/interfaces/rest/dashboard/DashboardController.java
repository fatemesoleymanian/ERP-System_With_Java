package com.example.minierp.interfaces.rest.dashboard;


import com.example.minierp.api.common.ApiResponse;
import com.example.minierp.application.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/dashboard/stats")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    public ResponseEntity<ApiResponse<KpiResponse>> KpiCards(){
        return ResponseEntity.ok(ApiResponse.success(service.getDashboardKpis()));
    }

    @GetMapping("/sales-chart")
    public ResponseEntity<ApiResponse<List<DashboardService.DailySales>>> getSalesChartData() {
        return ResponseEntity.ok(ApiResponse.success(service.getLast7DaysSales()));
    }
}
