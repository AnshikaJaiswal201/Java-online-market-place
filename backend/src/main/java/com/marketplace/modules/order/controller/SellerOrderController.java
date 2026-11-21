package com.marketplace.modules.order.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.order.dto.OrderResponse;
import com.marketplace.modules.order.dto.OrderStatusUpdateRequest;
import com.marketplace.modules.order.service.OrderService;
import com.marketplace.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
@PreAuthorize("hasAuthority('ROLE_SELLER')")
public class SellerOrderController {

    private final OrderService orderService;

    public SellerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getSellerOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderResponse> orders = orderService.getSellerOrders(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Seller incoming orders retrieved", orders));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse updated = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.ok("Order status updated successfully", updated));
    }
}
