package com.marketplace.modules.order.service;

import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.modules.inventory.entity.Inventory;
import com.marketplace.modules.inventory.repository.InventoryRepository;
import com.marketplace.modules.order.dto.CartItemRequest;
import com.marketplace.modules.order.dto.CartItemResponse;
import com.marketplace.modules.order.dto.CartSummaryResponse;
import com.marketplace.modules.order.entity.CartItem;
import com.marketplace.modules.order.repository.CartItemRepository;
import com.marketplace.modules.product.entity.Product;
import com.marketplace.modules.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           InventoryRepository inventoryRepository,
                           UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        return buildCartSummary(cartItems);
    }

    @Override
    @Transactional
    public CartSummaryResponse addToCart(Long userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (product.getIsActive() != 1) {
            throw new BadRequestException("Cannot add an inactive product to cart");
        }

        Inventory inventory = inventoryRepository.findByProduct_ProductId(product.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for product", "id", product.getProductId()));

        CartItem cartItem = cartItemRepository.findByUser_UserIdAndProduct_ProductId(userId, product.getProductId())
                .orElse(new CartItem(user, product, 0));

        int newQuantity = cartItem.getQuantity() + request.getQuantity();
        if (newQuantity > inventory.getStockQuantity()) {
            throw new BadRequestException(String.format("Requested quantity (%d) exceeds available stock (%d) for %s",
                    newQuantity, inventory.getStockQuantity(), product.getName()));
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    @Override
    @Transactional
    public CartSummaryResponse updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            Inventory inventory = inventoryRepository.findByProduct_ProductId(cartItem.getProduct().getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", cartItem.getProduct().getProductId()));

            if (quantity > inventory.getStockQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock: " + inventory.getStockQuantity());
            }

            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return getCart(userId);
    }

    @Override
    @Transactional
    public CartSummaryResponse removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to cart item");
        }

        cartItemRepository.delete(cartItem);
        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUser_UserId(userId);
    }

    private CartSummaryResponse buildCartSummary(List<CartItem> cartItems) {
        CartSummaryResponse summary = new CartSummaryResponse();
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;

        for (CartItem item : cartItems) {
            CartItemResponse res = new CartItemResponse();
            res.setCartItemId(item.getCartItemId());
            res.setProductId(item.getProduct().getProductId());
            res.setProductName(item.getProduct().getName());
            res.setSku(item.getProduct().getSku());
            res.setPrice(item.getProduct().getPrice());
            res.setImageUrl(item.getProduct().getImageUrl());
            res.setQuantity(item.getQuantity());

            BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            res.setSubtotal(subtotal);

            Inventory inv = inventoryRepository.findByProduct_ProductId(item.getProduct().getProductId()).orElse(null);
            res.setAvailableStock(inv != null ? inv.getStockQuantity() : 0);

            totalAmount = totalAmount.add(subtotal);
            totalCount += item.getQuantity();

            itemResponses.add(res);
        }

        summary.setItems(itemResponses);
        summary.setTotalAmount(totalAmount);
        summary.setTotalItemCount(totalCount);
        return summary;
    }
}
