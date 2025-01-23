package com.agri.vision.Repo;

import com.agri.vision.Model.Cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findById(Integer id);

    boolean existsByUsernameAndProductname(String usernameFromToken, String productname);

    List<Cart> findByUsername(String email);

    int countByUsername(String email);

}
