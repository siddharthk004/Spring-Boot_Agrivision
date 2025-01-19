package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.fungicide;

@Repository
public interface fungiRepo extends JpaRepository<fungicide, Long> {

    @Query("SELECT f FROM fungicide f WHERE f.productname = :productname")
    Optional<fungicide> findByProductName(@Param("productname") String ProductName);

    Optional<fungicide> findById(Integer id);

}
