package com.agri.vision.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.product;
import com.agri.vision.Repo.productRepo;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class searchController {

    @Autowired
    private productRepo productrepo;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Friday 24 jan 2025
    // Function : Search Query Dyanmically
    // give any query and also any case it will send the data to user
    ///////////////////////////////////////////
    @GetMapping("/user/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query) {
        List<product> pest = this.productrepo.findByProductnameContainingIgnoreCase(query);
        return ResponseEntity.ok(pest);
    }
}
