package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.user;


@Repository
@EnableJpaRepositories
public interface userRepo extends JpaRepository<user,Long> {

    user findByUsername(String username);

    @Query("select u from user u where u.username = :username")
    public user getUserByUserName(@Param("username") String username);

    boolean existsByEmail(String email);

    
    @Query("SELECT u.email FROM user u WHERE u.username = :username")
    String getEmailByUsername(@Param("username") String username);

    boolean existsByUsername(String usernameFromToken);

    Long getOtpByUsername(String usernameFromToken);

}
 
