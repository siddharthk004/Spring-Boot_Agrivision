package com.agri.vision.Repo;

import com.agri.vision.Model.Cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart, Long> {

    boolean existsByEmailAndProductname(String email, String productname);

    int countByEmail(String email);

    Optional<Cart> findById(Integer id);

    List<Cart> findByEmail(String email);

}
