package com.agri.vision.Repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.product;

@Repository
public interface productRepo extends JpaRepository<product, Long> {

    @Query("SELECT p FROM product p WHERE p.productname = :productname")
    Optional<product> findByProductName(@Param("productname") String ProductName);

    Optional<product> findById(Integer id);
    
    List<product> findByProductnameContainingIgnoreCase(String query);
     
    List<product> findByCategory(String category);

}
