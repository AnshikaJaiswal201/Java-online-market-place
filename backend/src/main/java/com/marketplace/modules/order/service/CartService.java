package com.marketplace.modules.order.service;

import com.marketplace.modules.order.dto.CartItemRequest;
import com.marketplace.modules.order.dto.CartSummaryResponse;

public interface CartService {
    CartSummaryResponse getCart(Long userId);
    CartSummaryResponse addToCart(Long userId, CartItemRequest request);
    CartSummaryResponse updateQuantity(Long userId, Long cartItemId, int quantity);
    CartSummaryResponse removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
