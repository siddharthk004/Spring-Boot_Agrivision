package com.agri.vision.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.service;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.servRepo;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.JwtService;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class serviceController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    public servRepo servrepo;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : friday 24 jan 2025
    // Function : save user report
    //   -give token and message and it will return the Success and confirm message and
    //       also it will save the user email and name and desc together
    ///////////////////////////////////////////
    @PostMapping("/user/serviceMsgSend")
    public ResponseEntity<String> service(
            @RequestHeader("Authorization") String token, // Get the token from the request header
            @RequestBody String message) { // Accept message in JSON format

        try {
            // Extract the username from the token (assuming "Bearer " prefix in the token)
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Find the existing user by username
            user existingUser = userRepo.findByUsername(usernameFromToken);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Retrieve user's email
            String userEmail = existingUser.getEmail();

            // Create and save the service entry in the database
            service newServiceEntry = new service();
            newServiceEntry.setUsername(existingUser.getUsername());
            newServiceEntry.setEmail(userEmail);
            newServiceEntry.setDescription(message);

            servrepo.save(newServiceEntry);

            // Prepare and return the confirmation message
            String confirmationMessage = String.format(
                    "Dear %s,\n\nI hope this message finds you well.\n\n" +
                            "I am pleased to inform you that the issue you reported has been resolved earlier than expected. "
                            +
                            "We have thoroughly tested the solution to ensure everything functions smoothly. " +
                            "Please feel free to verify it on your end and let us know if there’s anything else we can assist you with.\n\n"
                            +
                            "Thank you for your patience and trust in us.\n\nBest regards,\nSiddharth Kardile\nAgri-Vision",
                    existingUser.getUsername());

            return ResponseEntity.ok(confirmationMessage);

        } catch (Exception e) {
            // Handle exceptions (e.g., invalid token, database issues)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
