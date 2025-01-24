package com.agri.vision.Controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agri.vision.DTO.UserRegistrationRequest;
import com.agri.vision.Model.LoginReq;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.JwtService;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class userController {

    private static final Logger logger = LoggerFactory.getLogger(userController.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private userRepo userrepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void encodeExistingPasswords() {
        List<user> users = userrepo.findAll();
        for (user user : users) {
            if (!user.getPassword().startsWith("{")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userrepo.save(user);
            }
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 14 jan 2025
    // Function : Register New User
    // take username , email, endname, address, contact, occupation,password
    ///////////////////////////////////////////
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // Check if username already exists
        if (userrepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        // Check if email already exists
        if (userrepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken please choose different one!");
        }

        // Create a new user
        user newUser = new user();
        newUser.setUsername(request.getUsername());
        System.out.println("username " + request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setEndname(request.getEndname());
        newUser.setAddress(request.getAddress());
        newUser.setContact(request.getContact());
        newUser.setOccupation(request.getOccupation());
        System.out.println("mail id " + request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt the password

        // Save to database
        userrepo.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Thursday 14 jan 2025
    // Function : Login User
    // take usename and password and return the email username and token
    ///////////////////////////////////////////
    @PostMapping("/user/login")
    public ResponseEntity<?> signin(@RequestBody LoginReq loginRequest) {
        try {
            // Retrieve the user by username
            user user = userRepo.findByUsername(loginRequest.getUsername());

            // Compare the provided plain password with the hashed password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid username or password");
            }

            // Generate a JWT token using JwtService
            String token = jwtService.generateToken(user.getUsername());

            // Return a success response with the generated token
            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("token", token);
            logger.debug("Generated JWT Token: " + token);

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : View Profile Of USer
    // take token and return all the details of user
    ///////////////////////////////////////////
    @PostMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        // Correctly get the username from the Authentication object
        String username = authentication.getName();
        System.out.println(username);
        System.out.println(authentication);

        // Fetch the user from the database
        user user = userRepo.findByUsername(username);

        // If the user is not found, handle it appropriately
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Convert the image byte array to Base64 if the profile image exists
        String base64Image = null;
        if (user.getProfileImage() != null) {
            base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getProfileImage());
        }

        // Build the response with user details and Base64 image
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("image", base64Image); // Send Base64 image string
        response.put("email", user.getEmail());
        response.put("endname", user.getEndname());
        response.put("address", user.getAddress());
        response.put("contact", user.getContact());
        response.put("occupation", user.getOccupation());

        return ResponseEntity.ok(response);
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : edit Profile Of USer
    // give token and any field that we want to update and return success
    ///////////////////////////////////////////
    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(
            @RequestHeader("Authorization") String token, // Get the token from the request header
            @RequestBody UserRegistrationRequest request) { // Accept data in JSON format

        // Extract the username from the token
        String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in token

        // Find the existing user by username (from the token)
        user existingUser = userRepo.findByUsername(usernameFromToken);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Only allow certain fields to be updated (excluding username, email, and
        // password)
        if (request.getEndname() != null && !request.getEndname().isEmpty()) {
            existingUser.setEndname(request.getEndname());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            existingUser.setAddress(request.getAddress());
        }
        if (request.getContact() != null && !request.getContact().isEmpty()) {
            existingUser.setContact(request.getContact());
        }
        if (request.getOccupation() != null && !request.getOccupation().isEmpty()) {
            existingUser.setOccupation(request.getOccupation());
        }

        // Save the updated user to the database
        userrepo.save(existingUser);

        // Return the updated user profile (excluding password, email, username)
        Map<String, Object> response = new HashMap<>();
        response.put("username", existingUser.getUsername());
        response.put("endname", existingUser.getEndname());
        response.put("address", existingUser.getAddress());
        response.put("contact", existingUser.getContact());
        response.put("occupation", existingUser.getOccupation());
        response.put("profileImage",
                existingUser.getProfileImage() != null ? "Profile image updated" : "No profile image");

        return ResponseEntity.ok(response); // Return updated details
    }

    
}
