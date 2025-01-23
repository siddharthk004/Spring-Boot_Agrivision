package com.agri.vision.Repo;

import com.agri.vision.Model.wishlist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface wishlistRepo extends JpaRepository<wishlist, Long> {

    Optional<wishlist> findById(Integer id);

    boolean existsByUsernameAndProductname(String usernameFromToken, String productname);

    List<wishlist> findByUsername(String email);

    int countByUsername(String email);

}
