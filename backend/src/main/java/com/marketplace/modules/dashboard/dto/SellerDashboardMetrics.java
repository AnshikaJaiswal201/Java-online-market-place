package com.marketplace.modules.dashboard.dto;

import java.math.BigDecimal;

public class SellerDashboardMetrics {

    private BigDecimal totalRevenueInr;
    private long totalOrdersCount;
    private long activeProductsCount;
    private long lowStockCount;

    public SellerDashboardMetrics() {
        this.totalRevenueInr = BigDecimal.ZERO;
    }

    public SellerDashboardMetrics(BigDecimal totalRevenueInr, long totalOrdersCount,
                                  long activeProductsCount, long lowStockCount) {
        this.totalRevenueInr = totalRevenueInr != null ? totalRevenueInr : BigDecimal.ZERO;
        this.totalOrdersCount = totalOrdersCount;
        this.activeProductsCount = activeProductsCount;
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getTotalRevenueInr() {
        return totalRevenueInr;
    }

    public void setTotalRevenueInr(BigDecimal totalRevenueInr) {
        this.totalRevenueInr = totalRevenueInr;
    }

    public long getTotalOrdersCount() {
        return totalOrdersCount;
    }

    public void setTotalOrdersCount(long totalOrdersCount) {
        this.totalOrdersCount = totalOrdersCount;
    }

    public long getActiveProductsCount() {
        return activeProductsCount;
    }

    public void setActiveProductsCount(long activeProductsCount) {
        this.activeProductsCount = activeProductsCount;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }
}
