package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.Pdf;

@Repository
public interface PdfRepo extends JpaRepository<Pdf, Long> {
    // Define custom methods here, such as findByTitleOrAuthor
    
}