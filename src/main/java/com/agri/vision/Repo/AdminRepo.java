package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agri.vision.Model.Admin;

public interface AdminRepo extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}
