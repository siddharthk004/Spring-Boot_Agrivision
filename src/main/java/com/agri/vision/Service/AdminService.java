package com.agri.vision.Service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.agri.vision.Model.Admin;
import com.agri.vision.Repo.AdminRepo;

@Service
public class AdminService {

    @Autowired
    public AdminRepo adminRepo;

    public Admin createadmin() {
        Admin admin = new Admin();
        admin.setUsername("Admin");
        admin.setPassword("admin@123");
        return adminRepo.save(admin);

    }

}