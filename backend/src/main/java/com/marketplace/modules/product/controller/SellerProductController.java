package com.marketplace.modules.product.controller;

import com.marketplace.common.ApiResponse;
import com.marketplace.modules.product.dto.ProductRequest;
import com.marketplace.modules.product.dto.ProductResponse;
import com.marketplace.modules.product.service.ProductService;
import com.marketplace.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
@PreAuthorize("hasAuthority('ROLE_SELLER')")
public class SellerProductController {

    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSellerProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ProductResponse> products = productService.getSellerProducts(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Seller products retrieved successfully", products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(userDetails.getUserId(), request);
        return new ResponseEntity<>(ApiResponse.ok("Product created successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(userDetails.getUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleProductStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        ProductResponse response = productService.toggleProductStatus(userDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Product status toggled successfully", response));
    }
}
