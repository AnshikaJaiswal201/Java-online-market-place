package com.marketplace.modules.inventory.service;

import com.marketplace.exception.InsufficientStockException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.inventory.dto.InventoryResponse;
import com.marketplace.modules.inventory.dto.StockUpdateRequest;
import com.marketplace.modules.inventory.entity.Inventory;
import com.marketplace.modules.inventory.repository.InventoryRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getSellerInventory(Long sellerId) {
        return inventoryRepository.findAllBySellerId(sellerId).stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getSellerLowStockItems(Long sellerId) {
        return inventoryRepository.findLowStockBySellerId(sellerId).stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse updateStock(Long sellerId, Long inventoryId, StockUpdateRequest request) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", inventoryId));

        if (!inventory.getProduct().getSeller().getUserId().equals(sellerId)) {
            throw new AccessDeniedException("You are not authorized to update inventory for this product");
        }

        inventory.setStockQuantity(request.getStockQuantity());
        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }
        inventory.setLastRestockedAt(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inventory);
        return mapToInventoryResponse(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deductStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "productId", productId));

        if (inventory.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    inventory.getProduct().getSku(),
                    inventory.getStockQuantity(),
                    quantity
            );
        }

        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void restoreStock(Long productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "productId", productId));

        inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        InventoryResponse res = new InventoryResponse();
        res.setInventoryId(inventory.getInventoryId());
        res.setProductId(inventory.getProduct().getProductId());
        res.setProductName(inventory.getProduct().getName());
        res.setSku(inventory.getProduct().getSku());
        res.setPrice(inventory.getProduct().getPrice());
        res.setStockQuantity(inventory.getStockQuantity());
        res.setLowStockThreshold(inventory.getLowStockThreshold());
        res.setLowStock(inventory.isLowStock());
        res.setLastRestockedAt(inventory.getLastRestockedAt());
        return res;
    }
}
