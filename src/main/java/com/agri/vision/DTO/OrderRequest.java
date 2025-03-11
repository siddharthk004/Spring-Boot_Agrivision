package com.agri.vision.DTO;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {

    private Long userId;
    private List<OrderItemDTO> orderItems = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

}
