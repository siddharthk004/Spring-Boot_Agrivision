package com.agri.vision.Repo;

import com.agri.vision.Model.Cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Long> {

    // Fetch all cart items for a user
    List<Cart> findByUsername(String uname);
    Optional<Cart> findById(Integer id);
    int countByUsername(String uname);
    boolean existsByUsernameAndProductId(String usernameFromToken, Long productId);
}
