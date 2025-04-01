package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.vision.Model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findByUserId(Long id);

    Optional<Order> findById(Long id);

    Order findByIdAndUserId(Long orderId, Long userId);

}
