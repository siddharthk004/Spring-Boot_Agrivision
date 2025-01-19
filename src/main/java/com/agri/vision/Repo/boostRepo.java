package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.booster;

@Repository
public interface boostRepo extends JpaRepository<booster, Long> {

    @Query("SELECT f FROM booster f WHERE f.productname = :productname")
    Optional<booster> findByProductName(@Param("productname") String ProductName);

    Optional<booster> findById(Integer id);

}
