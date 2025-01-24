package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.service;

@Repository
public interface servRepo extends JpaRepository<service, Long> {
    Optional<service> findById(Integer id);
}
