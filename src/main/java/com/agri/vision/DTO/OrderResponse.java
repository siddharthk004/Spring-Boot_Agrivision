package com.agri.vision.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.agri.vision.Model.Order;


public class OrderResponse {

    private Long orderId;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private double subtotal;
    private String orderStatus;
    private String shippingAddress;
    private LocalDateTime orderDate;

    // Default constructor for Jackson
    public OrderResponse() {
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderResponse(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        this.orderId = order.getId();

        // Handle null user
        if (order.getUser() != null) {
            this.userId = order.getUser().getId();
        } else {
            this.userId = null; // or set a default value
        }

        this.subtotal = order.getSubTotal();
        this.orderStatus = order.getOrderstatus();
        this.shippingAddress = order.getShippingAddress();
        this.orderDate = order.getOrderDate();

        // Convert List<OrderItem> to List<OrderItemDTO>
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDTO(
                        orderItem.getProduct().getId(), // Assuming OrderItem has getProduct()
                        orderItem.getQuantity() // Assuming OrderItem has getQuantity()
                ))
                .collect(Collectors.toList());
    }
}