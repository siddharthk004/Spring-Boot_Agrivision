package com.agri.vision.Repo;

import com.agri.vision.Model.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface commentRepo extends JpaRepository<comment, Long> {

}