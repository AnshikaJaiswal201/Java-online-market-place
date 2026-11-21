package com.marketplace.modules.product.repository;

import com.marketplace.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByIsActiveOrderByCreatedAtDesc(Integer isActive);

    List<Product> findBySeller_UserIdOrderByCreatedAtDesc(Long sellerId);

    List<Product> findBySeller_UserIdAndIsActiveOrderByCreatedAtDesc(Long sellerId, Integer isActive);

    long countBySeller_UserIdAndIsActive(Long sellerId, Integer isActive);

    @Query("SELECT p FROM Product p WHERE p.isActive = 1 " +
           "AND (:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
           "AND (:keywordPattern IS NULL OR LOWER(p.name) LIKE :keywordPattern) " +
           "ORDER BY p.createdAt DESC")
    List<Product> searchActiveProducts(
            @Param("categoryId") Long categoryId,
            @Param("keywordPattern") String keywordPattern
    );
}
