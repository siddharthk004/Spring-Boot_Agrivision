package com.agri.vision.Repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.user;


@Repository
public interface userRepo extends JpaRepository<user,Long> {

    static user findByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUsername'");
    }

    @Query("select u from user u where u.username = :username")
    public user getUserByUserName(@Param("username") String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
 
    Optional<user> findByEmail(String email);
}
