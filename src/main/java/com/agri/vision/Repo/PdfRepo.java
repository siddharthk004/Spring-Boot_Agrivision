package com.agri.vision.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.Pdf;

@Repository
public interface PdfRepo extends JpaRepository<Pdf, Long> {
    // Define custom methods here, such as findByTitleOrAuthor
    
    @Query("SELECT p FROM Pdf p WHERE p.userid = :userid")
    List<Pdf> findAllByUserid(@Param("userid") Long userid);
}