package com.marketplace.modules.inventory.repository;

import com.marketplace.modules.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct_ProductId(Long productId);

    Optional<Inventory> findByProduct_Sku(String sku);

    @Query("SELECT COUNT(i) FROM Inventory i " +
           "WHERE i.product.seller.userId = :sellerId " +
           "AND i.product.isActive = 1 " +
           "AND i.stockQuantity <= i.lowStockThreshold")
    long countLowStockBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT i FROM Inventory i " +
           "WHERE i.product.seller.userId = :sellerId " +
           "AND i.product.isActive = 1 " +
           "AND i.stockQuantity <= i.lowStockThreshold")
    List<Inventory> findLowStockBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT i FROM Inventory i " +
           "WHERE i.product.seller.userId = :sellerId " +
           "ORDER BY i.product.createdAt DESC")
    List<Inventory> findAllBySellerId(@Param("sellerId") Long sellerId);
}
