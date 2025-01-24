package com.agri.vision.Repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.pesticide;

@Repository
public interface pestRepo extends JpaRepository<pesticide, Long> {

    @Query("SELECT p FROM pesticide p WHERE p.productname = :productname")
    Optional<pesticide> findByProductName(@Param("productname") String ProductName);

    Optional<pesticide> findById(Integer id);
    
    List<pesticide> findByProductnameContainingIgnoreCase(String query);

}
