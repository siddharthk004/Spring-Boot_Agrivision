package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.vision.Model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

}
