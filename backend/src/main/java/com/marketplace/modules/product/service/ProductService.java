package com.marketplace.modules.product.service;

import com.marketplace.modules.product.dto.CategoryDto;
import com.marketplace.modules.product.dto.ProductRequest;
import com.marketplace.modules.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(Long sellerId, ProductRequest request);
    ProductResponse updateProduct(Long sellerId, Long productId, ProductRequest request);
    ProductResponse toggleProductStatus(Long sellerId, Long productId);
    ProductResponse getProductById(Long productId);
    List<ProductResponse> getActiveProducts(Long categoryId, String keyword);
    List<ProductResponse> getSellerProducts(Long sellerId);
    List<CategoryDto> getAllCategories();
}
