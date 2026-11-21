package com.marketplace.modules.order.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusUpdateRequest {

    @NotBlank(message = "Order status is required")
    private String orderStatus; // PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
