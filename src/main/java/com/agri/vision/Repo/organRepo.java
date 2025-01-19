package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.organic;

@Repository
public interface organRepo extends JpaRepository<organic, Long> {

    @Query("SELECT f FROM organic f WHERE f.productname = :productname")
    Optional<organic> findByProductName(@Param("productname") String ProductName);

    Optional<organic> findById(Integer id);

}
