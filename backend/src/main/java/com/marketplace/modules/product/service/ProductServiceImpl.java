package com.marketplace.modules.product.service;

import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.modules.inventory.entity.Inventory;
import com.marketplace.modules.inventory.repository.InventoryRepository;
import com.marketplace.modules.product.dto.CategoryDto;
import com.marketplace.modules.product.dto.ProductRequest;
import com.marketplace.modules.product.dto.ProductResponse;
import com.marketplace.modules.product.entity.Category;
import com.marketplace.modules.product.entity.Product;
import com.marketplace.modules.product.repository.CategoryRepository;
import com.marketplace.modules.product.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              InventoryRepository inventoryRepository,
                              UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(Long sellerId, ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("Product with SKU already exists: " + request.getSku());
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku().toUpperCase().trim());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setSeller(seller);
        product.setIsActive(1);

        Product savedProduct = productRepository.save(product);

        Inventory inventory = new Inventory(
                savedProduct,
                request.getInitialStock() != null ? request.getInitialStock() : 0,
                request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5
        );
        inventoryRepository.save(inventory);

        return mapToProductResponse(savedProduct, inventory);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long sellerId, Long productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getUserId().equals(sellerId)) {
            throw new AccessDeniedException("You are not authorized to update this product");
        }

        // Check if SKU changed and already taken
        String newSku = request.getSku().toUpperCase().trim();
        if (!product.getSku().equalsIgnoreCase(newSku) && productRepository.existsBySku(newSku)) {
            throw new BadRequestException("SKU is already in use by another product: " + newSku);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(newSku);
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseGet(() -> inventoryRepository.save(new Inventory(updatedProduct, 0, 5)));

        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
            inventoryRepository.save(inventory);
        }

        return mapToProductResponse(updatedProduct, inventory);
    }

    @Override
    @Transactional
    public ProductResponse toggleProductStatus(Long sellerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getUserId().equals(sellerId)) {
            throw new AccessDeniedException("You are not authorized to modify this product");
        }

        int newStatus = (product.getIsActive() != null && product.getIsActive() == 1) ? 0 : 1;
        product.setIsActive(newStatus);
        Product savedProduct = productRepository.save(product);

        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId).orElse(null);
        return mapToProductResponse(savedProduct, inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId).orElse(null);
        return mapToProductResponse(product, inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts(Long categoryId, String keyword) {
        String keywordPattern = (keyword != null && !keyword.trim().isEmpty())
                ? "%" + keyword.trim().toLowerCase() + "%"
                : null;
        List<Product> products = productRepository.searchActiveProducts(categoryId, keywordPattern);

        return products.stream()
                .map(p -> {
                    Inventory inv = inventoryRepository.findByProduct_ProductId(p.getProductId()).orElse(null);
                    return mapToProductResponse(p, inv);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getSellerProducts(Long sellerId) {
        List<Product> products = productRepository.findBySeller_UserIdOrderByCreatedAtDesc(sellerId);
        return products.stream()
                .map(p -> {
                    Inventory inv = inventoryRepository.findByProduct_ProductId(p.getProductId()).orElse(null);
                    return mapToProductResponse(p, inv);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByIsActive(1).stream()
                .map(c -> new CategoryDto(c.getCategoryId(), c.getCategoryName(), c.getDescription()))
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product, Inventory inventory) {
        ProductResponse res = new ProductResponse();
        res.setProductId(product.getProductId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());
        res.setPrice(product.getPrice());
        res.setSku(product.getSku());
        res.setImageUrl(product.getImageUrl());
        res.setIsActive(product.getIsActive());
        res.setCreatedAt(product.getCreatedAt());
        res.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            res.setCategoryId(product.getCategory().getCategoryId());
            res.setCategoryName(product.getCategory().getCategoryName());
        }

        if (product.getSeller() != null) {
            res.setSellerId(product.getSeller().getUserId());
            res.setSellerName(product.getSeller().getFullName());
        }

        if (inventory != null) {
            res.setStockQuantity(inventory.getStockQuantity());
            res.setLowStockThreshold(inventory.getLowStockThreshold());
            res.setLowStock(inventory.isLowStock());
        } else {
            res.setStockQuantity(0);
            res.setLowStockThreshold(5);
            res.setLowStock(true);
        }

        return res;
    }
}
