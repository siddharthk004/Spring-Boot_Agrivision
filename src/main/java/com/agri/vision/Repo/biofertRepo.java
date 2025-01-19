package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.biofertilizer;

@Repository
public interface biofertRepo extends JpaRepository<biofertilizer, Long> {

    @Query("SELECT f FROM biofertilizer f WHERE f.productname = :productname")
    Optional<biofertilizer> findByProductName(@Param("productname") String ProductName);

    Optional<biofertilizer> findById(Integer id);

}
