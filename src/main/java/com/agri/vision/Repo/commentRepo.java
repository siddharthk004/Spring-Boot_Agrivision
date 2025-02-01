package com.agri.vision.Repo;

import com.agri.vision.Model.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface commentRepo extends JpaRepository<comment, Long> {

    @Query("SELECT c FROM comment c WHERE c.Pid = :id")
    List<comment> findByPid(@Param("id") Long id);
    
}