package com.marketplace.modules.order.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.order.dto.CartItemRequest;
import com.marketplace.modules.order.dto.CartSummaryResponse;
import com.marketplace.modules.order.service.CartService;
import com.marketplace.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartSummaryResponse summary = cartService.getCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Shopping cart retrieved", summary));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        CartSummaryResponse summary = cartService.addToCart(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", summary));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> updateQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {
        CartSummaryResponse summary = cartService.updateQuantity(userDetails.getUserId(), cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Cart item quantity updated", summary));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> removeFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long cartItemId) {
        CartSummaryResponse summary = cartService.removeFromCart(userDetails.getUserId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", summary));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared successfully", null));
    }
}
