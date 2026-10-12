package com.example.dogo_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.DashboardDTO;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.DashboardService;

/**
 * Resumen del panel de administración (solo ADMIN, ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardDTO>> resumen(
            @RequestParam(defaultValue = "5") int umbralStock) {
        int umbral = Math.max(0, Math.min(umbralStock, 1000));
        return ResponseEntity.ok(ApiResponse.success(dashboardService.resumen(umbral)));
    }
}
