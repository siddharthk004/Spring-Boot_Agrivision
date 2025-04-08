package com.agri.vision.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.agri.vision.DTO.OrderItemDTO;
import com.agri.vision.DTO.OrderRequest;
import com.agri.vision.DTO.OrderResponse;
import com.agri.vision.Model.Order;
import com.agri.vision.Model.OrderItem;
import com.agri.vision.Model.product;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.OrderRepo;
import com.agri.vision.Repo.productRepo;
import com.agri.vision.Repo.userRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final userRepo userrepo;
    private final productRepo productrepo;
    private final OrderRepo orderrepo;

    public OrderService(userRepo userrepo, productRepo productrepo, OrderRepo orderrepo) {
        this.userrepo = userrepo;
        this.productrepo = productrepo;
        this.orderrepo = orderrepo;
    }

    @Transactional
    public OrderResponse buyNow(OrderRequest orderRequest) {
        // Validate order request
        if (orderRequest == null || orderRequest.getUserId() == null || orderRequest.getOrderItems() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order request");
        }

        // Fetch user and validate address
        user user = userrepo.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User address not found");
        }

        // Create and save the order
        Order order = new Order();
        order.setUser(user);
        order.setOrderstatus("PLACED");
        order.setShippingAddress(user.getAddress());
        order.setSubTotal(0.0);
        order.setOrderDate(LocalDateTime.now());

        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        double subTotal = 0.0;

        for (OrderItemDTO itemDTO : orderRequest.getOrderItems()) {
            product product = productrepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            validateProductStock(product, itemDTO.getQuantity());

            OrderItem orderItem = createOrderItem(order, product, itemDTO.getQuantity());
            orderItems.add(orderItem);
            subTotal += orderItem.getPrice();

            updateProductStock(product, itemDTO.getQuantity());
        }

        // Update order with items and subtotal
        order.setOrderItems(orderItems);
        order.setSubTotal(subTotal);
        orderrepo.save(order);

        // Return response
        return mapToOrderResponse(order);
    }

    private void validateProductStock(product product, int requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product out of stock: " + product.getProductname());
        }
    }

    private OrderItem createOrderItem(Order order, product product, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getAfterdiscount() * quantity);
        orderItem.setCustomerId(order.getUser());
        return orderItem;
    }

    private void updateProductStock(product product, int soldQuantity) {
        product.setQuantity(product.getQuantity() - soldQuantity);
        product.setStatus(product.getQuantity() == 0 ? "Out Of Stock" : "Available");
        productrepo.save(product);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());

        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDTO(
                        orderItem.getProduct().getId(),
                        orderItem.getQuantity()))
                .collect(Collectors.toList());

        response.setOrderItems(orderItemDTOs);

        response.setSubtotal(order.getSubTotal());
        response.setOrderStatus(order.getOrderstatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setOrderDate(order.getOrderDate());

        return response;
    }

    @Transactional
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderrepo.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderrepo.findByUserId(userId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return mapToOrderResponse(order);
    }

    @Transactional
    public OrderResponse getOrderByIdForUser(Long orderId, Long userId) {
        Order order = orderrepo.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order not accessible");
        }
        return mapToOrderResponse(order);
    }
}