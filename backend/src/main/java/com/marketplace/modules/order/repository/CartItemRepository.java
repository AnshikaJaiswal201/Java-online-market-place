package com.marketplace.modules.order.repository;

import com.marketplace.modules.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    Optional<CartItem> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

    void deleteByUser_UserId(Long userId);
}
