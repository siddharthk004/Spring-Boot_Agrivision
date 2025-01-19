package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.fertilizer;

@Repository
public interface fertRepo extends JpaRepository<fertilizer, Long> {

    @Query("SELECT f FROM fertilizer f WHERE f.productname = :productname")
    Optional<fertilizer> findByProductName(@Param("productname") String ProductName);

    Optional<fertilizer> findById(Integer id);

}
