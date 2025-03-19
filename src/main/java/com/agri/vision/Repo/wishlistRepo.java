package com.agri.vision.Repo;

import com.agri.vision.Model.wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface wishlistRepo extends JpaRepository<wishlist, Long> {

    Optional<wishlist> findById(Long id);

    boolean existsByUsernameAndProductId(String username, Long productId);

    List<wishlist> findByUsername(String username);  // Corrected non-static implementation

    int countByUsername(String username);
}
