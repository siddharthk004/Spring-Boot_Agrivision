package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.userotp;


@Repository
public interface userOtpRepo extends JpaRepository<userotp,Long> {

    userotp findByUsername(String usernameFromToken);
}
 
