package com.marketplace.modules.dashboard.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.dashboard.dto.SellerDashboardMetrics;
import com.marketplace.modules.dashboard.service.SellerDashboardService;
import com.marketplace.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/dashboard")
@PreAuthorize("hasAuthority('ROLE_SELLER')")
public class SellerDashboardController {

    private final SellerDashboardService dashboardService;

    public SellerDashboardController(SellerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<SellerDashboardMetrics>> getDashboardMetrics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        SellerDashboardMetrics metrics = dashboardService.getSellerMetrics(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Seller metrics retrieved successfully", metrics));
    }
}
