package com.marketplace.modules.order.repository;

import com.marketplace.modules.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomer_UserIdOrderByCreatedAtDesc(Long customerId);

    @Query("SELECT COUNT(DISTINCT o.orderId) FROM Order o JOIN o.items i WHERE i.seller.userId = :sellerId")
    long countOrdersBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COALESCE(SUM(i.subtotal), 0) FROM OrderItem i WHERE i.seller.userId = :sellerId AND i.order.orderStatus != 'CANCELLED'")
    BigDecimal calculateTotalRevenueBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.seller.userId = :sellerId ORDER BY o.createdAt DESC")
    List<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId);
}
