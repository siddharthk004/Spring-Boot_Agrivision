package com.agri.vision.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.agri.vision.DTO.OrderRequest;
import com.agri.vision.DTO.OrderResponse;
import com.agri.vision.Model.Order;
import com.agri.vision.Service.OrderService;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/order/buy", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrderResponse> buyNow(@RequestBody OrderRequest orderRequest) {
        System.out.println("Hello Order");
        OrderResponse order = orderService.buyNow(orderRequest);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/order/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<Order> getOrderByIdForUser(
            @PathVariable Long userId, @PathVariable Long orderId) {
        Order order = orderService.getOrderByIdForUser(orderId, userId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(403).body(null); // Forbidden if order doesn't belong to user
        }
    }
}