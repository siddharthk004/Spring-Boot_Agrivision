package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.service;

@Repository
public interface servRepo extends JpaRepository<service, Long> {

    @Query("SELECT f FROM service f WHERE f.username = :username")
    Optional<service> findByProductName(@Param("username") String username);

    Optional<service> findById(Integer id);
}
