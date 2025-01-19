package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.nutrient;

@Repository
public interface nutriRepo extends JpaRepository<nutrient, Long> {

    @Query("SELECT f FROM nutrient f WHERE f.productname = :productname")
    Optional<nutrient> findByProductName(@Param("productname") String ProductName);

    Optional<nutrient> findById(Integer id);

}
