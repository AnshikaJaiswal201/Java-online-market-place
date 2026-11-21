package com.marketplace.modules.inventory.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.inventory.dto.InventoryResponse;
import com.marketplace.modules.inventory.dto.StockUpdateRequest;
import com.marketplace.modules.inventory.service.InventoryService;
import com.marketplace.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/inventory")
@PreAuthorize("hasAuthority('ROLE_SELLER')")
public class SellerInventoryController {

    private final InventoryService inventoryService;

    public SellerInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InventoryResponse> inventory = inventoryService.getSellerInventory(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Seller inventory retrieved", inventory));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStock(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InventoryResponse> lowStock = inventoryService.getSellerLowStockItems(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Low stock items retrieved", lowStock));
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long inventoryId,
            @Valid @RequestBody StockUpdateRequest request) {
        InventoryResponse response = inventoryService.updateStock(userDetails.getUserId(), inventoryId, request);
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", response));
    }
}
