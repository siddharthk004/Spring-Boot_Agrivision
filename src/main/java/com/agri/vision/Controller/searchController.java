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

import com.agri.vision.Model.pesticide;
import com.agri.vision.Repo.pestRepo;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class searchController {

    @Autowired
    private pestRepo pestrepo;

    // search Handler
    @GetMapping("/user/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query) {
        List<pesticide> pest = this.pestrepo.findByProductnameContainingIgnoreCase(query);
        return ResponseEntity.ok(pest);
    }
}
