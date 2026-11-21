package com.marketplace.modules.order.service;

import com.marketplace.common.Constants;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.modules.inventory.service.InventoryService;
import com.marketplace.modules.order.dto.CheckoutRequest;
import com.marketplace.modules.order.dto.OrderItemResponse;
import com.marketplace.modules.order.dto.OrderResponse;
import com.marketplace.modules.order.dto.OrderStatusUpdateRequest;
import com.marketplace.modules.order.entity.CartItem;
import com.marketplace.modules.order.entity.Order;
import com.marketplace.modules.order.entity.OrderItem;
import com.marketplace.modules.order.repository.CartItemRepository;
import com.marketplace.modules.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartItemRepository cartItemRepository,
                            UserRepository userRepository,
                            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderResponse checkout(Long customerId, CheckoutRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<CartItem> cartItems = cartItemRepository.findByUser_UserIdOrderByCreatedAtDesc(customerId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Your cart is empty. Add products before checking out.");
        }

        // Build Order
        Order order = new Order();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        order.setOrderNumber("ORD-" + dateStr + "-" + randomSuffix);
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI");
        order.setPaymentStatus(Constants.PAYMENT_STATUS_PAID); // Mock checkout payment success
        order.setOrderStatus(Constants.ORDER_STATUS_PLACED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getProductId();
            int quantity = cartItem.getQuantity();

            // Atomic inventory deduction with rollback protection
            inventoryService.deductStock(productId, quantity);

            BigDecimal unitPrice = cartItem.getProduct().getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProduct(),
                    cartItem.getProduct().getSeller(),
                    quantity,
                    unitPrice,
                    subtotal
            );
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful checkout
        cartItemRepository.deleteByUser_UserId(customerId);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomer_UserIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getSellerOrders(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        String targetStatus = request.getOrderStatus().toUpperCase().trim();

        // Restore inventory if an order is cancelled
        if (Constants.ORDER_STATUS_CANCELLED.equals(targetStatus) &&
                !Constants.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
            for (OrderItem item : order.getItems()) {
                inventoryService.restoreStock(item.getProduct().getProductId(), item.getQuantity());
            }
        }

        order.setOrderStatus(targetStatus);
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getOrderId());
        res.setOrderNumber(order.getOrderNumber());
        res.setCustomerId(order.getCustomer().getUserId());
        res.setCustomerName(order.getCustomer().getFullName());
        res.setCustomerEmail(order.getCustomer().getEmail());
        res.setTotalAmount(order.getTotalAmount());
        res.setOrderStatus(order.getOrderStatus());
        res.setShippingAddress(order.getShippingAddress());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setPaymentStatus(order.getPaymentStatus());
        res.setCreatedAt(order.getCreatedAt());
        res.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setOrderItemId(item.getOrderItemId());
            ir.setProductId(item.getProduct().getProductId());
            ir.setProductName(item.getProduct().getName());
            ir.setSku(item.getProduct().getSku());
            ir.setImageUrl(item.getProduct().getImageUrl());
            ir.setSellerId(item.getSeller().getUserId());
            ir.setSellerName(item.getSeller().getFullName());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setSubtotal(item.getSubtotal());
            return ir;
        }).collect(Collectors.toList());

        res.setItems(itemResponses);
        return res;
    }
}
