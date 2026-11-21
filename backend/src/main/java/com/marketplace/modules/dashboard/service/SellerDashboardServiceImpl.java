package com.marketplace.modules.dashboard.service;

import com.marketplace.modules.dashboard.dto.SellerDashboardMetrics;
import com.marketplace.modules.inventory.repository.InventoryRepository;
import com.marketplace.modules.order.repository.OrderRepository;
import com.marketplace.modules.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SellerDashboardServiceImpl implements SellerDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public SellerDashboardServiceImpl(OrderRepository orderRepository,
                                      ProductRepository productRepository,
                                      InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SellerDashboardMetrics getSellerMetrics(Long sellerId) {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenueBySellerId(sellerId);
        long totalOrders = orderRepository.countOrdersBySellerId(sellerId);
        long activeProducts = productRepository.countBySeller_UserIdAndIsActive(sellerId, 1);
        long lowStockCount = inventoryRepository.countLowStockBySellerId(sellerId);

        return new SellerDashboardMetrics(totalRevenue, totalOrders, activeProducts, lowStockCount);
    }
}
