package com.agri.vision.Repo;

import com.agri.vision.Model.wishlist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface wishlistRepo extends JpaRepository<wishlist, Long> {

    boolean existsByEmailAndProductname(String email, String productname);

    int countByEmail(String email);

    Optional<wishlist> findById(Integer id);

    List<wishlist> findByEmail(String email);

}
