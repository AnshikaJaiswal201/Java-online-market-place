package com.marketplace.modules.dashboard.service;

import com.marketplace.modules.dashboard.dto.SellerDashboardMetrics;

public interface SellerDashboardService {
    SellerDashboardMetrics getSellerMetrics(Long sellerId);
}
