package com.agri.vision.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.service;
import com.agri.vision.Repo.servRepo;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class serviceController {

    @Autowired
    public servRepo servrepo;

    // add or save the new user here  *  help center  *
    @PostMapping("/user/service/save")
    String newUser(@RequestBody service newUser) {
        servrepo.save(newUser);
        String message = "Dear \n  ,I hope this message finds you well.\n pleased to inform you that the issue you reported has been resolved earlier than expected. We have thoroughly tested the solution to ensure everything functions smoothly. Please feel free to verify it on your end and let us know if there’s anything else we can assist you with.\n Thank you for your patience and trust in us.\nBest regards,\nSiddharth Kardile\nAgri-Vision";
        return message;
    }
}
