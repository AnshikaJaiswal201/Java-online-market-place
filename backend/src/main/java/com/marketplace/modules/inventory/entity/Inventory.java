package com.marketplace.modules.inventory.entity;

import com.marketplace.modules.product.entity.Product;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTORY")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVENTORY_ID")
    private Long inventoryId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRODUCT_ID", unique = true, nullable = false)
    private Product product;

    @Column(name = "STOCK_QUANTITY", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "LOW_STOCK_THRESHOLD", nullable = false)
    private Integer lowStockThreshold = 5;

    @Column(name = "LAST_RESTOCKED_AT")
    private LocalDateTime lastRestockedAt = LocalDateTime.now();

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Inventory() {
    }

    public Inventory(Product product, Integer stockQuantity, Integer lowStockThreshold) {
        this.product = product;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = (lowStockThreshold != null) ? lowStockThreshold : 5;
        this.lastRestockedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return stockQuantity != null && lowStockThreshold != null && stockQuantity <= lowStockThreshold;
    }

    // Getters and Setters
    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public LocalDateTime getLastRestockedAt() {
        return lastRestockedAt;
    }

    public void setLastRestockedAt(LocalDateTime lastRestockedAt) {
        this.lastRestockedAt = lastRestockedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
