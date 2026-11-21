package com.marketplace.modules.order.service;

import com.marketplace.modules.order.dto.CheckoutRequest;
import com.marketplace.modules.order.dto.OrderResponse;
import com.marketplace.modules.order.dto.OrderStatusUpdateRequest;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(Long customerId, CheckoutRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getCustomerOrders(Long customerId);
    List<OrderResponse> getSellerOrders(Long sellerId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request);
}
