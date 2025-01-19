package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.insecticide;

@Repository
public interface insectRepo extends JpaRepository<insecticide, Long> {

    @Query("SELECT f FROM insecticide f WHERE f.productname = :productname")
    Optional<insecticide> findByProductName(@Param("productname") String ProductName);

    Optional<insecticide> findById(Integer id);

}
