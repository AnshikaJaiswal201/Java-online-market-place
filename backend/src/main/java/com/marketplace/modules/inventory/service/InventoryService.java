package com.marketplace.modules.inventory.service;

import com.marketplace.modules.inventory.dto.InventoryResponse;
import com.marketplace.modules.inventory.dto.StockUpdateRequest;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getSellerInventory(Long sellerId);
    List<InventoryResponse> getSellerLowStockItems(Long sellerId);
    InventoryResponse updateStock(Long sellerId, Long inventoryId, StockUpdateRequest request);
    void deductStock(Long productId, int quantity);
    void restoreStock(Long productId, int quantity);
}
