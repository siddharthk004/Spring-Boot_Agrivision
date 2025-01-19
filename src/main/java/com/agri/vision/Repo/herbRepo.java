package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.herbicide;

@Repository
public interface herbRepo extends JpaRepository<herbicide, Long> {

    @Query("SELECT f FROM herbicide f WHERE f.productname = :productname")
    Optional<herbicide> findByProductName(@Param("productname") String ProductName);

    Optional<herbicide> findById(Integer id);

}
