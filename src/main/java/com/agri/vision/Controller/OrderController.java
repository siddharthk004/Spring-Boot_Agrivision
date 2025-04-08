package com.agri.vision.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
        OrderResponse order = orderService.buyNow(orderRequest);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/admin/order/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PostMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrderByIdForUser(@PathVariable Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdForUser(orderId, userId));
    }

}